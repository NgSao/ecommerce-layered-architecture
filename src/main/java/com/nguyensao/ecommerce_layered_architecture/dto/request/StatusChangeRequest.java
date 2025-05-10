package com.nguyensao.ecommerce_layered_architecture.dto.request;

import com.nguyensao.ecommerce_layered_architecture.enums.StatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusChangeRequest {

    private String id;

    private StatusEnum status;
}
