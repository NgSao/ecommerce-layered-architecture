package com.nguyensao.ecommerce_layered_architecture.dto.response;

import java.time.Instant;

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
    String fullName;
    String phone;
    String email;
    String profileImageUrl;
    RoleAuthorities role;
    StatusEnum status;
    Instant birthday;
    Instant lastLoginDate;
    Instant createdAt;
}
