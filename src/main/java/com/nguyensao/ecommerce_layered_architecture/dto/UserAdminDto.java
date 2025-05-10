package com.nguyensao.ecommerce_layered_architecture.dto;

import java.time.Instant;
import java.util.Set;

import com.nguyensao.ecommerce_layered_architecture.enums.GenderEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;
import com.nguyensao.ecommerce_layered_architecture.enums.StatusEnum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAdminDto {
    String id;
    String fullName;
    String password;
    String email;
    String phone;
    Instant birthday;
    GenderEnum gender;
    String profileImageUrl;
    String refreshToken;
    Instant lastLoginDate;
    RoleAuthorities role;
    StatusEnum status;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    Set<AddressDto> addresses;
    Set<OrderDto> orders;
    int totalOrders;
    int totalPrice;

}