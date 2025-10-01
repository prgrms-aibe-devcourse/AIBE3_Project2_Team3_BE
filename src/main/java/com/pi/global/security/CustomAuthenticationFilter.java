package com.pi.global.security;

import com.pi.domain.user.user.entity.Users;
import com.pi.domain.user.user.service.AuthTokenService;
import com.pi.domain.user.user.service.UserService;
import com.pi.global.exception.ServiceException;
import com.pi.global.rq.Rq;
import com.pi.global.rsData.RsData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final Rq rq;
    private final UserService userService;
    private final AuthTokenService authTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            work(request, response, filterChain);
        } catch (ServiceException e) {
            RsData<Void> rsData = e.getRsData();
            response.setContentType("application/json");
            response.setStatus(rsData.statusCode());
            //response.getWriter().write()
        } catch (Exception e) {
            throw e;
        }
    }

    private void work(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // API 요청 아니라면 패스
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 인증, 인가가 필요없는 API 요청 이라면 패스
        if (List.of("/api/v1/users/login", "/api/v1/users/logout", "/api/v1/users/join").contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = null;
        String refreshToken = null;

        String headerAuthorization = rq.getHeader("Authorization", "");

        // headerAuthorization이 존재한다면
        if (!headerAuthorization.isBlank()) {
            if (!headerAuthorization.startsWith("Bearer ")) {
                throw new ServiceException("401-2", "인증 정보가 올바르지 않습니다.");
            }

            // ["Bearer", accessToken]
            String[] headerAuthorizations = headerAuthorization.split(" ", 3);

            accessToken = headerAuthorizations[1];
            refreshToken = headerAuthorizations.length == 3 ? headerAuthorizations[2] : "";
        } else { // headerAuthorization이 존재하지 않는다면 쿠키에서 정보 가져오기
            accessToken = rq.getCookieValue("accessToken", "");
            refreshToken = rq.getCookieValue("refreshToken", "");
        }

        logger.debug("accessToken: " + accessToken);
        logger.debug("refreshToken: " + refreshToken);

        if (accessToken.isBlank() && refreshToken.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        Users users = null;
        boolean isAccessTokenExists = !accessToken.isBlank();
        boolean isAccessTokenValid = false;

        if (isAccessTokenExists) {
            Map<String, Object> payload = authTokenService.payload(accessToken);

            if (payload != null) {
                long id = ((Number) payload.get("id")).longValue();
                String username = (String) payload.get("username");
                String nickname = (String) payload.get("nickname");
                String role = (String) payload.get("role");
                users = new Users(id, username, nickname);

                // 토큰 유효성 검증 성공
                isAccessTokenValid = true;
            }
        }

        // accessToken이 유효하지 않거나 없을 때 refreshToken을 이용하여 새로운 accessToken 발급
        if (users == null && !refreshToken.isBlank()) {
            users = userService.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new ServiceException("401-3", "회원을 찾을 수 없습니다."));
        }

        // 토큰이 존재하고, accessToken 유효성 검증 실패 시
        if (isAccessTokenExists && !isAccessTokenValid) {
            // refreshToken을 이용해 새로운 accessToken을 발급
            String actorAccessToken = userService.genAccessToken(users);

            rq.setCookie("accessToken", actorAccessToken); // 새로운 accessToken을 쿠키에 설정
            rq.setHeader("Authorization", "Bearer " + actorAccessToken); // Authorization 헤더에 새로운 토큰 설정
        }

        UserDetails user = new SecurityUser(
                users.getId(),
                users.getUsername(),
                "",
                users.getNickname(),
                users.getAuthorities()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                "",
                user.getAuthorities()
        );

        // 이 시점 이후부터는 시큐리티가 이 요청을 인증된 사용자의 요청으로 취급
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
