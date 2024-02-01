package com.ttarum.s3.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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

    public URL uploadImage(byte[] imageBytes) {
        String key = UUID.randomUUID().toString();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(imageBytes));
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key));
    }
}
