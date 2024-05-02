package com.ttarum.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewListResponseForSpecificMember {

    private List<ReviewResponse> reviewList;
}
