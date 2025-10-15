package com.pi.domain.post.file.controller;

import com.pi.domain.post.file.service.FreelancerFileService;
import com.pi.global.s3.AwsS3DownloadDto;
import com.pi.global.s3.AwsS3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/freelancers/{freelancerId}/files")
@RequiredArgsConstructor
@Tag(name = "ApiV1FreelancerFileController", description = "API 프리랜서 파일 컨트롤러")
public class ApiV1FreelancerFileController {
    private final FreelancerFileService freelancerFileService;
    private final AwsS3Service awsS3Service;

    @GetMapping("/{id}")
    @Operation(summary = "프리랜서 파일 다운로드")
    public ResponseEntity<Resource> downloadFile(@PathVariable long freelancerId, @PathVariable long id) {
        String fileUrl = freelancerFileService.findById(id).getUrl();

        AwsS3DownloadDto fileData = awsS3Service.getDownloadData(fileUrl);
        String encodedFileName = URLEncoder.encode(fileData.fileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .contentType(MediaType.parseMediaType(fileData.contentType()))
                .body(fileData.resource());
    }
}
