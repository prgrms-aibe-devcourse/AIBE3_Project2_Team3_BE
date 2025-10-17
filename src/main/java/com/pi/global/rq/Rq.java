package com.pi.global.rq;

import com.pi.domain.user.user.entity.User;
import com.pi.domain.user.user.entity.UserRole;
import com.pi.global.exception.ServiceException;
import com.pi.global.security.SecurityUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;

    public User getActor() {
        return Optional.ofNullable(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                )
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof SecurityUser)
                .map(principal -> (SecurityUser) principal)
                .map(securityUser -> {
                    boolean isAdmin = securityUser.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .anyMatch(a -> "ROLE_ADMIN".equals(a));

                    UserRole role = isAdmin ? UserRole.ROLE_ADMIN : UserRole.ROLE_USER;

                    return new User(securityUser.getId(), securityUser.getUsername(), securityUser.getNickname(), role);
                })
                .orElse(null);
    }

    public User getActorOrNull() {
        try {
            return getActor();
        } catch (Exception e) {
            return null;
        }
    }

    public void setHeader(String name, String value) {
        if (value == null) value = "";

        if (value.isBlank()) {
            req.removeAttribute(name);
        } else {
            resp.setHeader(name, value);
        }
    }

    public String getHeader(String name, String defaultValue) {
        return Optional
                .ofNullable(req.getHeader("Authorization"))
                .filter(headerValue -> !headerValue.isBlank())
                .orElse(defaultValue);
    }

    public String getCookieValue(String name, String defaultValue) {
        return Optional
                .ofNullable(req.getCookies())
                .flatMap(
                        cookies ->
                                Arrays.stream(req.getCookies())
                                        .filter(cookie -> name.equals(cookie.getName()))
                                        .map(Cookie::getValue)
                                        .findFirst()
                )
                .orElse(defaultValue);
    }

    public void setCookie(String name, String value) {
        boolean delete = (value == null) || value.isBlank();
        String host = req.getServerName();
        boolean isLocal = "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host);

        String domain = System.getenv("COOKIE_DOMAIN"); // 로컬이면 비워두기
        boolean secure = Boolean.parseBoolean(System.getenv().getOrDefault("COOKIE_SECURE",
                isLocal ? "false" : "true"));
        boolean crossSite = Boolean.parseBoolean(System.getenv().getOrDefault("COOKIE_CROSS_SITE",
                isLocal ? "false" : "true")); // prod: true

        // 1) 표준 Cookie로 기본 속성
        Cookie cookie = new Cookie(name, delete ? "" : value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setMaxAge(delete ? 0 : 60 * 60 * 24 * 365);

        // 로컬에서는 domain 지정하지 말기 (HostOnly 쿠키)
        if (!isLocal && domain != null && !domain.isBlank()) {
            cookie.setDomain(domain.startsWith(".") ? domain : domain);
        }

        resp.addCookie(cookie);

        // 2) SameSite는 컨테이너별 편차가 있으므로 Set-Cookie 헤더로 확정
        String sameSite = crossSite ? "None" : "Lax"; // cross-site면 무조건 None
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(delete ? "" : value)
                .append("; Path=/")
                .append("; Max-Age=").append(delete ? 0 : 60 * 60 * 24 * 365)
                .append("; HttpOnly");
        if (!isLocal && domain != null && !domain.isBlank()) sb.append("; Domain=").append(domain);
        if (secure) sb.append("; Secure");
        sb.append("; SameSite=").append(sameSite);

        resp.addHeader("Set-Cookie", sb.toString());
    }

    public void deleteCookie(String name) {
        setCookie(name, null);
    }

    public SecurityUser getSecurityUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof SecurityUser securityUser) {
            return securityUser;
        }

        // 인증되지 않았거나 다른 타입인 경우 예외 처리
        throw new ServiceException("401-4", "로그인 정보가 유효하지 않습니다.");
    }
}