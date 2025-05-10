package com.nguyensao.ecommerce_layered_architecture.dto.response;

import java.util.Set;

import com.nguyensao.ecommerce_layered_architecture.dto.AddressDto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AdminUserDto {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private Set<AddressDto> address;

}
