package com.nguyensao.ecommerce_layered_architecture.dto.request;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;
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
public class AuthRegisterRequest {
    @NotBlank(message = "Họ và tên không được bỏ trống")
    String fullName;

    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Email không hợp lệ")
    String email;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    String phone;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    String password;
}
