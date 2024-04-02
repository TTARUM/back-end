package com.ttarum.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.ttarum.common.annotation.resolver.VerificationUserResolver;
import com.ttarum.common.dto.user.LoggedInUser;

/**
 * {@link LoggedInUser}에 데이터를 매핑하기 위해 사용되는 애노테이션
 * {@link VerificationUserResolver}와 함께 사용됩니다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface VerificationUser {
}
