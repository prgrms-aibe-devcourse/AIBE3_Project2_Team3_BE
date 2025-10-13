package com.pi.global.security;

import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.service.AuthTokenService;
import com.pi.domain.user.user.service.RefreshTokenStore;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final Rq rq;
    private final AuthTokenService authTokenService;
    private final RefreshTokenStore refreshTokenStore; // authVersion 조회 등
    private final UserService userService;             // 필요 시 DB에서 유저/권한 로드

    private static final Set<String> AUTH_WHITELIST = Set.of(
            "/api/v1/users/login",
            "/api/v1/users/logout",
            "/api/v1/users/join",
            "/api/v1/users/findPw"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Preflight(OPTIONS)은 그대로 통과
            if (HttpMethod.OPTIONS.matches(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            final String uri = request.getRequestURI();

            // API/WS만 보호, 그 외는 패스
            boolean needsAuth = uri.startsWith("/api/") || uri.startsWith("/ws/");
            if (!needsAuth) {
                filterChain.doFilter(request, response);
                return;
            }

            // 화이트리스트는 패스
            if (AUTH_WHITELIST.contains(uri)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 1) Access Token 검사 (헤더 → 쿠키)
            String accessToken = resolveAccessToken();
            if (!accessToken.isBlank()) {
                Map<String, Object> claims = authTokenService.payload(accessToken);
                if (claims != null) {
                    setAuthenticationFromClaims(claims); // 내부에서 authVersion 체크 & 권한 세팅
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            // 2) Refresh Token로 회복 (쿠키 전용)
            String refreshPlain = rq.getCookieValue("refreshToken", "");
            if (!refreshPlain.isBlank()) {
                try {
                    // Redis에서 소유자(userId) 확인
                    long userId = authTokenService.findRefreshOwner(refreshPlain);

                    // 회전 + 새 AT 발급
                    String newRefresh = authTokenService.rotateRefresh(refreshPlain);
                    User owner = userService.getById(userId);      // 최신 유저(권한/닉네임 등)
                    String newAccess = authTokenService.genAccessToken(owner);

                    // 쿠키 갱신
                    rq.setCookie("refreshToken", newRefresh);
                    rq.setCookie("accessToken", newAccess);

                    // 현재 요청 인증 확정
                    setAuthenticationFromUser(owner);

                    filterChain.doFilter(request, response);
                    return;
                } catch (ServiceException e) {
                    // 회복 실패 → 401
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            // 3) 둘 다 없으면 익명으로 통과(컨트롤러에서 권한에 따라 401/403 처리)
            filterChain.doFilter(request, response);

        } catch (ServiceException e) {
            // 서비스 레벨 예외는 상태코드만 세팅하고 종료
            response.setStatus(e.getRsData().statusCode());
        }
    }

    /** Authorization: Bearer ... → or cookie(accessToken) */
    private String resolveAccessToken() {
        String header = rq.getHeader("Authorization", "");
        if (!header.isBlank() && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length()).trim();
        }
        return rq.getCookieValue("accessToken", "");
    }

    /** AT claims로부터 인증 세팅 (authVersion 즉시성 체크 포함) */
    private void setAuthenticationFromClaims(Map<String, Object> claims) {
        long id = ((Number) claims.get("id")).longValue();
        String username = (String) claims.get("username");
        String nickname = (String) claims.get("nickname");

        // 1) authVersion 즉시성: Redis의 버전과 다르면 재로그인 유도
        long tokenVer = toLong(claims.getOrDefault("authVersion", 1));
        long serverVer = refreshTokenStore.getAuthVersion(id);
        if (serverVer != tokenVer) {
            throw new ServiceException("401-9", "권한 정보가 변경되었습니다. 재로그인 해주세요.");
        }

        // 2) roles 사용: AT에 있으면 그걸 쓰고, 없으면 최소 ROLE_USER
        List<String> roles = extractRolesFromClaims(claims);
        List<SimpleGrantedAuthority> authorities = toAuthorities(roles);

        SecurityUser principal = new SecurityUser(id, username, "", nickname, authorities);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /** DB(User) 객체 기반 인증 세팅 */
    private void setAuthenticationFromUser(User user) {
        var authorities = user.getAuthorities(); // User.getAuthorities()는 ROLE_ 접두사 포함해야 함
        SecurityUser principal = new SecurityUser(
                user.getId(), user.getUsername(), "", user.getNickname(), authorities
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /** claims.roles → ["ROLE_X", ...] 로 정규화 */
    @SuppressWarnings("unchecked")
    private List<String> extractRolesFromClaims(Map<String, Object> claims) {
        Object rolesObj = claims.get("roles");
        List<String> raw;
        if (rolesObj instanceof String) {
            raw = List.of((String) rolesObj);
        } else if (rolesObj instanceof Collection<?>) {
            raw = ((Collection<?>) rolesObj).stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } else {
            raw = List.of("ROLE_USER"); // 최소 권한
        }

        // 접두사 정규화 + 중복 제거
        return raw.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .distinct()
                .toList();
    }

    private List<SimpleGrantedAuthority> toAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private long toLong(Object n) {
        if (n instanceof Number) return ((Number) n).longValue();
        try {
            return Long.parseLong(String.valueOf(n));
        } catch (Exception e) {
            return 1L;
        }
    }
}