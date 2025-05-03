package com.nguyensao.ecommerce_layered_architecture.dto.request;

import java.time.Instant;

import com.nguyensao.ecommerce_layered_architecture.enums.GenderEnum;

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
public class UserUpdateRequest {
    String fullName;
    String phone;
    String profileImageUrl;
    Instant birthday;
    GenderEnum gender;
}