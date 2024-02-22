package com.ttarum.inquiry.service;

import com.ttarum.inquiry.domain.Inquiry;
import com.ttarum.inquiry.domain.InquiryImage;
import com.ttarum.inquiry.exception.InquiryImageException;
import com.ttarum.inquiry.exception.InquiryNotFoundException;
import com.ttarum.inquiry.repository.InquiryImageRepository;
import com.ttarum.inquiry.repository.InquiryRepository;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InquiryImageService {

    private final InquiryRepository inquiryRepository;
    private final InquiryImageRepository inquiryImageRepository;
    private final ImageService imageService;

    /**
     * 문의글의 이미지를 저장한다.
     * {@code saveImage} 메서드를 호출하여 저장한다.
     *
     * @param inquiryId 이미지가 저장될 문의글의 ID
     * @param imageList 저장할 이미지 파일 리스트
     */
    @Transactional
    public void saveImageList(final long inquiryId, final List<MultipartFile> imageList) {
        imageList.forEach(image -> saveImage(inquiryId, image));
    }

    /**
     * 문의글의 이미지를 저장한다.
     *
     * @param inquiryId 이미지가 저장될 문의글의 ID
     * @param image     저장할 이미지 파일
     * @throws InquiryNotFoundException 문의글이 존재하지 않는 경우 발생한다.
     * @throws InquiryImageException    파일이 존재하지 않는 경우, 이미지 파일이 아닌 경우 발생하는 예외
     */
    @Transactional
    public void saveImage(final long inquiryId, final MultipartFile image) {
        try {
            validateImageFile(image);
            URL url = imageService.saveImage(image);
            Inquiry inquiry = getInquiryByInquiryId(inquiryId);
            InquiryImage inquiryImage = InquiryImage.of(url.getPath(), inquiry);

            inquiryImageRepository.save(inquiryImage);
        } catch (IOException e) {
            throw new InquiryImageException(HttpStatus.BAD_REQUEST, "이미지 저장에 실패했습니다.", e);
        }
    }

    private Inquiry getInquiryByInquiryId(final long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(InquiryNotFoundException::new);
    }

    @SuppressWarnings({"null", "DataFlowIssue"})
    public void validateImageFile(final MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty())
            throw new InquiryImageException(HttpStatus.BAD_REQUEST, "파일이 존재하지 않습니다.");


        if (!file.getContentType().startsWith("image/"))
            throw new InquiryImageException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드가 가능합니다.");
    }
}
