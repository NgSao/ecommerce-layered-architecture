package com.nguyensao.ecommerce_layered_architecture.dto.response;

import java.time.Instant;
import java.util.Set;

import com.nguyensao.ecommerce_layered_architecture.dto.AddressDto;
import com.nguyensao.ecommerce_layered_architecture.enums.GenderEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;
import com.nguyensao.ecommerce_layered_architecture.enums.StatusEnum;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCustomerResponse {
    String id;
    String fullName;
    String phone;
    String email;
    String profileImageUrl;
    RoleAuthorities role;
    StatusEnum status;
    GenderEnum gender;
    Instant birthday;
    Instant lastLoginDate;
    Instant createdAt;
    Set<AddressDto> addresses;
}
