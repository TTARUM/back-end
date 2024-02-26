package com.ttarum.common.annotation.resolver;

import com.ttarum.common.annotation.VerificationUser;
import com.ttarum.common.dto.user.User;
import io.micrometer.common.lang.NonNullApi;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

/**
 * {@link VerificationUser} 애노테이션과 함께 {@link User}를 파라미터로 받을 경우
 * {@link HttpServletRequest}에서 authentication 애트리뷰트를 꺼내 해당 {@link User}에 매핑합니다.
 */
@NonNullApi
public class VerificationUserResolver implements HandlerMethodArgumentResolver {

    private static final String AUTHENTICATION = "authentication";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        VerificationUser annotation = parameter.getParameterAnnotation(VerificationUser.class);
        return Objects.nonNull(annotation) && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        return request.getAttribute(AUTHENTICATION);
    }
}