package com.ttarum.common.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class LoggedInUser implements User {

    private final Long id;

    @Override
    public boolean isVerification() {
        return true;
    }
}
