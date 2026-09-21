package com.ttarum.review.service;

import com.ttarum.review.domain.Review;
import com.ttarum.review.domain.ReviewImage;
import com.ttarum.review.exception.ReviewImageException;
import com.ttarum.review.exception.ReviewNotFoundException;
import com.ttarum.review.repository.ReviewImageRepository;
import com.ttarum.review.repository.ReviewRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewImageServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ImageService imageService;

    @Mock
    ReviewImageRepository reviewImageRepository;

    @InjectMocks
    ReviewImageService reviewImageService;

    @Test
    @DisplayName("리뷰의 이미지 저장")
    void saveImageList() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "originalFilename.jpeg",
                "image/jpeg",
                "test file".getBytes(StandardCharsets.UTF_8));
        URL url = new URL("https", "host", 80, "file.jpeg");
        long reviewId = 1L;
        Review review = Review.builder()
                .id(reviewId)
                .title("title")
                .content("content")
                .build();


        when(imageService.saveImage(multipartFile)).thenReturn(url);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewImageRepository.save(any())).thenReturn(ReviewImage.builder().id(1L).build());

        // when
        reviewImageService.saveImageList(reviewId, List.of(multipartFile));

        // then
        verify(imageService, times(1)).saveImage(multipartFile);
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewImageRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("파일이 비어있을 경우 예외가 발생한다.")
    void saveImageListFailByEmptyFile() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "originalFilename.jpeg",
                "image/jpeg",
                new byte[0]);
        long reviewId = 1L;

        // when & then
        assertThatThrownBy(() -> reviewImageService.saveImageList(reviewId, List.of(multipartFile)))
                .isInstanceOf(ReviewImageException.class)
                .hasMessage("파일이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("이미지 파일이 아닌 경우 예외가 발생한다.")
    void saveImageListFailByNotImageFile() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "originalFilename.jpeg",
                "application/pdf",
                "test file".getBytes(StandardCharsets.UTF_8));
        long reviewId = 1L;

        // when & then
        assertThatThrownBy(() -> reviewImageService.saveImageList(reviewId, List.of(multipartFile)))
                .isInstanceOf(ReviewImageException.class)
                .hasMessage("이미지 파일만 업로드가 가능합니다.");
    }

    @Test
    @DisplayName("리뷰글이 존재하지 않는 경우 예외가 발생한다.")
    void saveImageListFailByInvalidReview() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "originalFilename.jpeg",
                "image/jpeg",
                "test file".getBytes(StandardCharsets.UTF_8));

        URL url = new URL("https", "host", 80, "file.jpeg");
        long reviewId = 1L;

        when(imageService.saveImage(multipartFile)).thenReturn(url);
        when(reviewRepository.findById(reviewId)).thenThrow(new ReviewNotFoundException());

        // when & then
        assertThatThrownBy(() -> reviewImageService.saveImageList(reviewId, List.of(multipartFile)))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    @DisplayName("IOException이 발생한 경우 ReviewImageException으로 예외가 다시 발생한다.")
    void saveImageListFailByImageServiceFailure() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "originalFilename.jpeg",
                "image/jpeg",
                "test file".getBytes(StandardCharsets.UTF_8));

        long reviewId = 1L;

        when(imageService.saveImage(multipartFile)).thenThrow(new IOException());

        // when & then
        assertThatThrownBy(() -> reviewImageService.saveImageList(reviewId, List.of(multipartFile)))
                .isInstanceOf(ReviewImageException.class);
    }
}