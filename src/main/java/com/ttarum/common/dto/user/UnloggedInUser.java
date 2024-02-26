package com.ttarum.common.dto.user;

public class UnloggedInUser implements User {
    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public long getId() {
        throw new UnsupportedOperationException("This method is not supported");
    }
}
