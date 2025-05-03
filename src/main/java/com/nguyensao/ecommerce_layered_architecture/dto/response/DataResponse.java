package com.nguyensao.ecommerce_layered_architecture.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataResponse<T> {
    int status;
    String error;
    Object message;
    T data;
}