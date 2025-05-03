package com.nguyensao.ecommerce_layered_architecture.dto.request;

import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleChangeRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Schema(description = "Email người dùng cần thay đổi quyền", example = "user@example.com")
    private String email;

    @NotNull(message = "Quyền không được để trống")
    @Schema(description = "Quyền mới muốn gán cho người dùng", example = "CUSTOMER")
    private RoleAuthorities roleAuthorities;
}
