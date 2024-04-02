package com.ttarum.inquiry.domain;

import com.ttarum.inquiry.exception.InquiryImageException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "inquiry_image")
public class InquiryImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int")
    private Long id;

    @Column(name = "file_url", nullable = false, length = 100)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    public static InquiryImage of(final String fileUrl, final Inquiry inquiry) {
        InquiryImage inquiryImage = InquiryImage.builder()
                .fileUrl(fileUrl)
                .inquiry(inquiry)
                .build();

        inquiryImage.validate();
        return inquiryImage;
    }

    private void validate() {
        if (!StringUtils.hasText(fileUrl)) {
            throw new InquiryImageException(HttpStatus.INTERNAL_SERVER_ERROR, "파일의 URL이 비어있습니다.");
        }
    }
}