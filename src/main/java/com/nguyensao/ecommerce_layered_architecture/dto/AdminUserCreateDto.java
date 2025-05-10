package com.nguyensao.ecommerce_layered_architecture.dto;

import java.util.Set;

import lombok.Data;

@Data
public class AdminUserCreateDto {
    private String email;
    private String phone;
    private String fullName;
    private String password;
    private Set<AdminUserAddressDto> address;
}
