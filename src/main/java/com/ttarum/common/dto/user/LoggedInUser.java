package com.ttarum.common.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class LoggedInUser implements User {

    private final Long id;

    @Override
    public long getId() {
        return id;
    }
}
