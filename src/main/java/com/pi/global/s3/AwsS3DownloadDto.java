package com.pi.global.s3;

import org.springframework.core.io.InputStreamResource;

public record AwsS3DownloadDto(
        String fileName,
        String contentType,
        InputStreamResource resource
) {
}
