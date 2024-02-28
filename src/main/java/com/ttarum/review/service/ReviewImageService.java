package com.ttarum.review.service;

import com.ttarum.review.domain.Review;
import com.ttarum.review.domain.ReviewImage;
import com.ttarum.review.exception.ReviewImageException;
import com.ttarum.review.exception.ReviewNotFoundException;
import com.ttarum.review.repository.ReviewImageRepository;
import com.ttarum.review.repository.ReviewRepository;
import com.ttarum.s3.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewImageService {

    private final ImageService imageService;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    /**
     * 리뷰의 이미지를 저장한다.
     *
     * @param reviewId          리뷰의 Id 값
     * @param multipartFileList 저장될 이미지 리스트
     */
    @Transactional
    public void saveImageList(final long reviewId, final List<MultipartFile> multipartFileList) {
        try {
            for (int i = 0; i < multipartFileList.size(); i++) {
                MultipartFile image = multipartFileList.get(i);
                validateImageFile(image);
                URL url = imageService.saveImage(image);
                Review review = getReviewByInquiryId(reviewId);
                ReviewImage reviewImage = ReviewImage.of(url.getPath(), review, i + 1);

                reviewImageRepository.save(reviewImage);
            }
        } catch (IOException e) {
            throw new ReviewImageException(HttpStatus.BAD_REQUEST, "이미지 저장에 실패했습니다.", e);
        }
    }

    private Review getReviewByInquiryId(final long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
    }

    @SuppressWarnings({"null", "DataFlowIssue"})
    public void validateImageFile(final MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty())
            throw new ReviewImageException(HttpStatus.BAD_REQUEST, "파일이 존재하지 않습니다.");


        if (!file.getContentType().startsWith("image/"))
            throw new ReviewImageException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드가 가능합니다.");
    }
}
