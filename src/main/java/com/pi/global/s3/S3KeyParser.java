package com.pi.global.s3;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class S3KeyParser {
    public String getDecodedFileName(String fileUrl) {
        String key = extractKey(fileUrl);
        String original = originalNameFromKey(key);
        return URLDecoder.decode(original, StandardCharsets.UTF_8);
    }

    public String extractKey(String fileUrl) {
        // URL의 path 전체를 key로 사용 (쿼리/서명 제거)
        var uri = URI.create(fileUrl);
        String path = uri.getPath(); // e.g. /dir/uuid_name.png
        if (path == null || path.isBlank()) throw new IllegalArgumentException("Invalid S3 URL");
        return path.startsWith("/") ? path.substring(1) : path;
    }

    public String originalNameFromKey(String fileKey) {
        int idx = fileKey.indexOf('_');
        if (idx < 0 || idx == fileKey.length() - 1) return fileKey; // 혹시 규칙이 깨진 경우 방어
        return fileKey.substring(idx + 1);
    }
}
