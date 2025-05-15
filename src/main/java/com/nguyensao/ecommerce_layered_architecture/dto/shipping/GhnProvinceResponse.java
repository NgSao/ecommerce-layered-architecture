package com.nguyensao.ecommerce_layered_architecture.dto.shipping;

import java.util.List;

import lombok.Data;

@Data
public class GhnProvinceResponse {
    private int code;
    private String message;
    private List<GhnProvince> data;
}
