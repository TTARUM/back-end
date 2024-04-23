package com.ttarum.s3.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Component
public class S3Component {
    private final S3Client s3Client;
    private final String bucketName = "ttarum-bucket";

    public S3Component(@Value("${cloud.aws.credentials.accessKey}") String accessKey,
                       @Value("${cloud.aws.credentials.secretKey}") String secretKey,
                       @Value("${cloud.aws.region.static}") String region) {
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .build();
    }

    /**
     * {@code file}를 AWS S3에 업로드한 후 해당 URL을 반환합니다.
     *
     * @param file {@code file}
     * @return 업로드된 이미지의 URL
     */
    public URL uploadImage(MultipartFile file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder(UUID.randomUUID().toString());
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension != null) {
            stringBuilder.append(".").append(extension);
        }
        String key = stringBuilder.toString();

        byte[] bytes = file.getBytes();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(bytes));
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key));
    }
}
