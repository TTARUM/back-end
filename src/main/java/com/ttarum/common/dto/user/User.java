package com.ttarum.common.dto.user;

public interface User {

    boolean isLoggedIn();

    /**
     * 유저의 Id 값을 반환합니다.
     * isLoggedIn 메서드를 통해 true 값을 반환받았을 경우에만 사용해야 합니다.
     * @throws UnsupportedOperationException 로그인하지 않은 유저가 이 메서드를 사용했을 경우 발생합니다.
     * @return User의 Id값
     */
    long getId();
}
