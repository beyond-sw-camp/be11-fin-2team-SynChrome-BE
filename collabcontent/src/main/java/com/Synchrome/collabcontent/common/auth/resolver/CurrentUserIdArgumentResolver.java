package com.Synchrome.collabcontent.common.auth.resolver;

import com.Synchrome.collabcontent.common.auth.annotation.CurrentUserId;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class) &&
                parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws MissingRequestHeaderException {
        String userIdHeader = webRequest.getHeader("X-User-Id");
        if (userIdHeader == null) {
            throw new MissingRequestHeaderException("X-User-Id", parameter);
        }
        return Long.parseLong(userIdHeader);
    }
}
