package com.pi.domain.application.file.controller;

import com.pi.domain.application.file.service.ApplicationFileService;
import com.pi.global.s3.AwsS3DownloadDto;
import com.pi.global.s3.AwsS3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/applications/{applicationId}/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "ApiV1ApplicationFileController", description = "API 구직 파일 컨트롤러")
public class ApiV1ApplicationFileController {
    private static final String AWS_S3_DIRECTORY = "test";

    private final ApplicationFileService applicationFileService;
    private final AwsS3Service awsS3Service;

    @GetMapping("/{id}")
    @Operation(summary = "파일 다운로드")
    public ResponseEntity<Resource> downloadFile(@PathVariable long applicationId, @PathVariable long id) {
        String fileUrl = applicationFileService.findById(id).getUrl();

        AwsS3DownloadDto fileData = awsS3Service.getDownloadData(fileUrl);
        String encodedFileName = URLEncoder.encode(fileData.fileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.parseMediaType(fileData.contentType()))
                .body(fileData.resource());
    }
}
