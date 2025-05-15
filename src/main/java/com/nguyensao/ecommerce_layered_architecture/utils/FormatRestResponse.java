package com.nguyensao.ecommerce_layered_architecture.utils;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.nguyensao.ecommerce_layered_architecture.annotation.AppMessage;
import com.nguyensao.ecommerce_layered_architecture.dto.response.DataResponse;
import com.nguyensao.ecommerce_layered_architecture.constant.ResponseConstant;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
            Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request,
            ServerHttpResponse response) {

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        String path = request.getURI().getPath();

        if (path.startsWith(ResponseConstant.SWAGGER_DOCS_PATH) || path.startsWith(ResponseConstant.SWAGGER_UI_PATH)) {
            return body;
        }

        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        if (status >= 400) {
            return body;
        }

        DataResponse<Object> restResponse = new DataResponse<>();
        restResponse.setStatus(status);
        restResponse.setData(body);
        AppMessage message = methodParameter.getMethodAnnotation(AppMessage.class);
        restResponse.setMessage(message != null ? message.value() : ResponseConstant.DEFAULT_SUCCESS_MESSAGE);

        return restResponse;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}
