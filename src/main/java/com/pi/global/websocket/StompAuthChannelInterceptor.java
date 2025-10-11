package com.pi.global.websocket;

import com.pi.global.security.CustomUserDetailsService;
import com.pi.global.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    // 크로스 도메인 환경이라 쿠키를 못 쓰는 경우가 있으므로 웹소켓은 헤더 인증을 권장한다고 함
    private final CustomUserDetailsService userDetailsService;

    @Value("${custom.jwt.secretKey}")
    private String jwtSecretKey;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (acc != null && StompCommand.CONNECT.equals(acc.getCommand())) {
            String token = acc.getFirstNativeHeader("Authorization"); // "Bearer xxx"
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Missing Authorization header");
            }
            String jwtStr = token.startsWith("Bearer ") ? token.substring(7) : token;

            if (!Ut.jwt.isValid(jwtSecretKey, jwtStr)) {
                throw new IllegalArgumentException("Invalid JWT");
            }
            Map<String, Object> p = Ut.jwt.payload(jwtSecretKey, jwtStr);
            if (p == null) throw new IllegalArgumentException("Invalid JWT payload");

            String username = (String) p.get("username");
            if (username == null) throw new IllegalArgumentException("username claim missing");

            UserDetails user = userDetailsService.loadUserByUsername(username);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
            acc.setUser(auth);
        }
        return message;
    }
}
