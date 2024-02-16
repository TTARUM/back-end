package com.ttarum.inquiry.service;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.domain.InquiryImage;
import com.ttarum.inquiry.exception.InquiryImageException;
import com.ttarum.inquiry.exception.InquiryNotFoundException;
import com.ttarum.inquiry.repository.InquiryImageRepository;
import com.ttarum.inquiry.repository.InquiryRepository;
import com.ttarum.s3.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquiryImageServiceTest {

    @Mock
    InquiryRepository inquiryRepository;
    @Mock
    InquiryImageRepository inquiryImageRepository;
    @Mock
    ImageService imageService;
    @InjectMocks
    InquiryImageService inquiryImageService;

    @Test
    @DisplayName("문의글의 이미지를 저장할 수 있다.")
    void saveImage() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "originalFilename.jpeg",
                "image/jpeg",
                "test file".getBytes(StandardCharsets.UTF_8));
        URL url = new URL("https", "host", 80, "file.jpeg");
        long inquiryId = 1L;
        Inquiry inquiry = Inquiry.builder()
                .id(inquiryId)
                .title("title")
                .content("content")
                .isSecret(false)
                .build();


        // when
        when(imageService.saveImage(multipartFile)).thenReturn(url);
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(inquiry));
        when(inquiryImageRepository.save(any())).thenReturn(InquiryImage.builder().id(1L).build());
        inquiryImageService.saveImage(inquiryId, multipartFile);

        // then
        verify(imageService, times(1)).saveImage(multipartFile);
        verify(inquiryRepository, times(1)).findById(inquiryId);
        verify(inquiryImageRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("파일이 비어있을 경우 예외가 발생한다.")
    void saveImageFailureByEmptyFile() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "originalFilename.jpeg",
                "image/jpeg",
                new byte[0]);
        long inquiryId = 1L;

        // when

        // then
        assertThatThrownBy(() -> inquiryImageService.saveImage(inquiryId, multipartFile))
                .isInstanceOf(InquiryImageException.class)
                .hasMessage("파일이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("이미지 파일이 아닌 경우 예외가 발생한다.")
    void saveImageFailureByNotImageFile() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "originalFilename.jpeg",
                "application/pdf",
                "test file".getBytes(StandardCharsets.UTF_8));
        long inquiryId = 1L;

        // when

        // then
        assertThatThrownBy(() -> inquiryImageService.saveImage(inquiryId, multipartFile))
                .isInstanceOf(InquiryImageException.class)
                .hasMessage("이미지 파일만 업로드가 가능합니다.");
    }

    @Test
    @DisplayName("문의글이 존재하지 않는 경우 예외가 발생한다.")
    void saveImageFailureByInvalidInquiry() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "originalFilename.jpeg",
                "image/jpeg",
                "test file".getBytes(StandardCharsets.UTF_8));

        URL url = new URL("https", "host", 80, "file.jpeg");
        long inquiryId = 1L;


        // when
        when(imageService.saveImage(multipartFile)).thenReturn(url);
        when(inquiryRepository.findById(inquiryId)).thenThrow(new InquiryNotFoundException());

        // then
        assertThatThrownBy(() -> inquiryImageService.saveImage(inquiryId, multipartFile))
                .isInstanceOf(InquiryNotFoundException.class);
    }
}