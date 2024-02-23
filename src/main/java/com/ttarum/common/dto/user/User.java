package com.ttarum.common.dto.user;

public interface User {

    boolean isLoggedIn();

    /**
     * 회원의 Id 값을 반환합니다.
     * {@link #isLoggedIn() isLoggedIn} 메서드를 통해 {@code true} 값을 반환받았을 경우에만 사용해야 합니다.
     *
     * @return 회원의 Id 값
     * @throws UnsupportedOperationException 로그인하지 않은 사용자가 이 메서드를 사용했을 경우 발생합니다.
     */
    long getId();
}
