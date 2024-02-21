package com.ttarum.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartAdditionRequest {

    private final long itemId;
    private final int amount;
}
