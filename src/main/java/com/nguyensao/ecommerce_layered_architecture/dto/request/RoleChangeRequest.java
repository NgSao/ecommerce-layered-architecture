package com.nguyensao.ecommerce_layered_architecture.dto.request;

import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleChangeRequest {

    private String id;

    private RoleAuthorities roleAuthorities;
}
