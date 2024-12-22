package org.noisevisionproductions.portfolio.auth.exceptions;

import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

class MockHttpMessageConverter {
    static MethodParameter mockMethodParameter() {
        Method method;
        try {
            method = MockHttpMessageConverter.class.getDeclaredMethod("mockMethod");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return new MethodParameter(method, -1);
    }

    private void mockMethod() {
    }
}
