package com.ttarum.s3.service;

import com.ttarum.s3.component.S3Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    @Mock
    private S3Component s3Component;

    @InjectMocks
    private ImageService imageService;

    @Test
    void whenSaveImage_thenS3ComponentUploadImageAndReturnUrl() throws IOException {
        // given
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        URL expectedUrl = new URL("https://ttarum-bucket.s3.ap-northeast-2.amazonaws.com/uuid_test.jpg");
        when(s3Component.uploadImage(file.getBytes()))
                .thenReturn(expectedUrl);

        // when
        URL resultUrl = imageService.saveImage(file);

        // then
        assertEquals(expectedUrl, resultUrl, "The URL is returned from S3Component.uploadImage()");
    }

}