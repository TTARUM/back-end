package com.ttarum.common.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class LoggedInUser {

    private final Long id;

    public long getId() {
        return id;
    }
}
