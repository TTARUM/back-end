package com.ttarum.review.validator;

import com.ttarum.review.exception.ReviewException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ReviewValidator {

    public void validateRating(final short rating) {
        if (rating < 0 || rating > 5) {
            throw new ReviewException(HttpStatus.BAD_REQUEST, "평점은 0 ~ 5의 숫자만 가능합니다.");
        }
    }
}
