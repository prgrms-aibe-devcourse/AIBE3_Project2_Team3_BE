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
        if (value == null) value = "";

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/"); // 쿠키를 도메인 전체에서 쓰겠다.
        cookie.setHttpOnly(true); // 쿠키를 스크립트로 접근 못하게(XSS 공격방어)
        cookie.setDomain("localhost"); // 쿠키가 적용될 도메인 지정
        cookie.setSecure(false); // https 에서만 쿠키전송
        cookie.setAttribute("SameSite", "Strict"); // 동일 사이트에서만 쿠키 전송(CSRF 공격방어)

        // 값이 없다면 해당 변수를 삭제하라는 뜻
        if (value.isBlank()) {
            cookie.setMaxAge(0);
        } else {
            cookie.setMaxAge(60 * 60 * 24 * 365); // 1년
        }

        resp.addCookie(cookie);
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