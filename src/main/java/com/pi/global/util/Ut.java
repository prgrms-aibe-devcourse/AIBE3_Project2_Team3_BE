package com.pi.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi.global.rsData.PageMeta;
import com.pi.global.rsData.PagePayload;
import com.pi.global.rsData.SortOrder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.domain.Page;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Ut {
    public static class jwt {
        private static final SecureRandom R = new SecureRandom();

        public static String toString(String secret, int expireSeconds, Map<String, Object> body) {
            ClaimsBuilder claimsBuilder = Jwts.claims();

            for (Map.Entry<String, Object> entry : body.entrySet()) {
                claimsBuilder.add(entry.getKey(), entry.getValue());
            }

            Claims claims = claimsBuilder.build();

            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

            Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            String jwt = Jwts.builder()
                    .claims(claims)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();

            return jwt;
        }

        public static boolean isValid(String secret, String jwtStr) {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            try {
                Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwtStr);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        public static Map<String, Object> payload (String secret, String jwtStr) {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            try {
                return (Map<String, Object>) Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwtStr)
                        .getPayload();
            } catch (Exception e) {
                return null;
            }
        }

        public static String newOpaqueToken(int bytes) {
            byte[] buf = new byte[bytes]; // 64 권장
            R.nextBytes(buf);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
        }

        public static String sha256(String s) {
            try {
                var md = MessageDigest.getInstance("SHA-256");
                return HexFormat.of().formatHex(md.digest(s.getBytes(StandardCharsets.UTF_8)));
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static class json {
        public static ObjectMapper objectMapper;

        public static String toString(Object object) {
            return toString(object, null);
        }

        public static String toString(Object object, String defaultValue) {
            try {
                return objectMapper.writeValueAsString(object);
            } catch (Exception e) {
                return defaultValue;
            }
        }
    }

    public static class pageMapper {
        public static <T> PagePayload<T> of(Page<T> p) {
            List<SortOrder> sort = new ArrayList<>();
            p.getSort().forEach(o -> sort.add(new SortOrder(o.getProperty(), o.getDirection().name())));

            PageMeta meta = new PageMeta(
                    p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(),
                    p.isFirst(), p.isLast(), p.hasNext(), p.hasPrevious(), sort
            );
            return new PagePayload<>(p.getContent(), meta);
        }
    }
}
