package com.pi.global.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {
    private final AmazonS3 amazonS3;
    private final S3KeyParser s3KeyParser;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileKey = generateUniqueFileName(file.getOriginalFilename());
            String fileUrl = upload(file, fileKey);
            fileUrls.add(fileUrl);
        }
        return fileUrls;
    }

    public List<String> uploadFiles(List<MultipartFile> files, String directoryName) {
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileKey = directoryName + "/" + generateUniqueFileName(file.getOriginalFilename());
            String fileUrl = upload(file, fileKey);
            fileUrls.add(fileUrl);
        }
        return fileUrls;
    }

    public AwsS3DownloadDto getDownloadData(String fileUrl) {
        String fileKey = getFileKey(fileUrl);
        String decodedFileKey = URLDecoder.decode(fileKey, StandardCharsets.UTF_8);
        S3Object s3Object = amazonS3.getObject(bucket, decodedFileKey);

        String fileName = s3KeyParser.originalNameFromKey(fileKey);
        String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);

        String contentType = s3Object.getObjectMetadata().getContentType();
        InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

        return new AwsS3DownloadDto(decodedFileName, contentType, resource);
    }

    public void deleteFile(String fileKey) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileKey));
    }

    private String upload(MultipartFile file, String fileKey) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, fileKey, inputStream, objectMetadata));
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패", e);
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }

        return amazonS3.getUrl(bucket, fileKey).toString();
    }

    private String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID() + "_" + originalFileName;
    }

    private String getFileKey(String fileUrl) {
        String delimiter = ".com/";
        int index = fileUrl.indexOf(delimiter);
        if (index == -1) {
            throw new IllegalArgumentException("Invalid S3 File URL");
        }
        return fileUrl.substring(index + delimiter.length());
    }

    public String getDecodedFileKey(String fileUrl) {
        String fileKey = getFileKey(fileUrl);
        return URLDecoder.decode(fileKey, StandardCharsets.UTF_8);
    }
}
