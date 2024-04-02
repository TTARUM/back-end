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

    /**
     * {@link MultipartFile file}을 AWS S3에 저장합니다.
     * {@link S3Component}의 {@link S3Component#uploadImage(byte[] imageBytes) upload} 메서드를 호출합니다.
     *
     * @param file 저장될 파일
     * @return 저장된 파일의 URL
     * @throws IOException I/O 에러가 일어날 경우 발생합니다.
     */
    public URL saveImage(MultipartFile file) throws IOException {
        return s3Component.uploadImage(file.getBytes());
    }
}
