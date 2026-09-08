package hu.viktor.catalogservice.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3PosterService {

    private final S3Template s3Template;

    @Value("${catalog.s3.bucket-name}")
    private String bucketName;

    public String uploadPoster(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();

        s3Template.upload(bucketName, filename, file.getInputStream());

        return "http://localhost:4566/" + bucketName + "/" + filename;
    }
}
