package com.pi.global.security;

import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.service.AuthTokenService;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final Rq rq;
    private final AuthTokenService authTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1) API 보호 대상이 아니면 패스
        String uri = request.getRequestURI();
        boolean needsAuth = uri.startsWith("/api/") || uri.startsWith("/ws/");
        if (!needsAuth) {
            filterChain.doFilter(request, response);
            return;
        }
        // 2) 화이트리스트 패스
        if (List.of("/api/v1/users/login", "/api/v1/users/logout", "/api/v1/users/join", "/api/v1/users/findPw")
                .contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            /* ---------- 액세스 토큰 처리 (헤더 우선 → 쿠키) ---------- */
            String header = rq.getHeader("Authorization", "");
            String accessToken = (!header.isBlank() && header.startsWith("Bearer "))
                    ? header.substring("Bearer ".length()).trim()
                    : rq.getCookieValue("accessToken", "");

            if (!accessToken.isBlank()) {
                Map<String, Object> claims = authTokenService.payload(accessToken);
                if (claims != null) {
                    setAuthenticationFromClaims(claims);
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            /* ---------- 리프레시로 회복 (쿠키 전용) ---------- */
            String refreshPlain = rq.getCookieValue("refreshToken", "");
            if (!refreshPlain.isBlank()) {
                try {
                    // 1) 소유자 확인 (fetch join → LAZY 예외 방지)
                    User owner = authTokenService.findActiveRefreshOwner(refreshPlain);

                    // 2) 회전 + 새 액세스 발급
                    String newRefresh = authTokenService.rotateRefresh(refreshPlain);
                    String newAccess  = authTokenService.genAccessToken(owner);

                    // 3) 쿠키 갱신 (여기서 응답 쿠키 세팅)
                    rq.setCookie("refreshToken", newRefresh);
                    rq.setCookie("accessToken", newAccess);

                    // 4) 현재 요청 인증 확정 (헤더 수정은 현재 요청에 의미 없음)
                    setAuthenticationFromUser(owner);

                    filterChain.doFilter(request, response);
                    return;

                } catch (ServiceException e) {
                    // 회복 실패 → 401로 종료 (컨트롤러 진입 차단)
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            // 액세스/리프레시 모두 없음 → 익명으로 통과
            filterChain.doFilter(request, response);

        } catch (ServiceException e) {
            response.setStatus(e.getRsData().statusCode());
        }
    }

    private void setAuthenticationFromClaims(Map<String, Object> claims) {
        long id = ((Number) claims.get("id")).longValue();
        String username = (String) claims.get("username");
        String nickname = (String) claims.get("nickname");
        String role = (String) claims.getOrDefault("role", "ROLE_USER");

        SecurityUser principal = new SecurityUser(
                id, username, "", nickname,
                List.of(() -> role)
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setAuthenticationFromUser(User user) {
        SecurityUser principal = new SecurityUser(
                user.getId(), user.getUsername(), "", user.getNickname(), user.getAuthorities()
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
