package com.ttarum.s3.service;

import com.ttarum.s3.component.S3Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Service
public class ImageService {
    private final S3Component s3Component;

    public ImageService(S3Component s3Component) {
        this.s3Component = s3Component;
    }

    public URL saveImage(MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        return s3Component.uploadImage(imageBytes);
    }
}
