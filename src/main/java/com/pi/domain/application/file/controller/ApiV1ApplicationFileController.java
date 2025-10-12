package com.pi.domain.application.file.controller;

import com.pi.global.s3.AwsS3DownloadDto;
import com.pi.global.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TestApplicationFileController {
    private static final String AWS_S3_DIRECTORY = "test";

    private final AwsS3Service awsS3Service;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFile(List<MultipartFile> files) {
        log.info("파일 저장 컨트롤러 실행");
        try {
            return ResponseEntity.ok(awsS3Service.uploadFile(files, AWS_S3_DIRECTORY));
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileUrl) {
        AwsS3DownloadDto fileData = awsS3Service.getDownloadData(fileUrl);
        String encodedFileName = URLEncoder.encode(fileData.fileName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.parseMediaType(fileData.contentType()))
                .body(fileData.resource());
    }

    @DeleteMapping("/upload")
    public ResponseEntity deleteFile(@RequestParam String fileKey) {
        log.info("파일 삭제 컨트롤러 실행");
        try {
            awsS3Service.deleteFile(fileKey);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("파일 삭제 실패", e);
            return ResponseEntity.status(400).build();
        }
    }
}
