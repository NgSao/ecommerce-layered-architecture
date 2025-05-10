package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserAddressDto {
    private String fullName;
    private String phone;
    private String city;
    private String district;
    private String street;
    private String addressDetail;

}
