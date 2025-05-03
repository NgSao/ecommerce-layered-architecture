package com.nguyensao.ecommerce_layered_architecture.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderEnum {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String gender;
}
