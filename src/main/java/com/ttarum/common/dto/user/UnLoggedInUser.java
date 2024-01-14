package com.ttarum.common.dto.user;

public class UnLoggedInUser implements User {
    @Override
    public boolean isLoggedIn() {
        return false;
    }
}
