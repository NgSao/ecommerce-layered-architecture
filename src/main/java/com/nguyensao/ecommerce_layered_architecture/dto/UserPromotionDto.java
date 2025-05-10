package com.nguyensao.ecommerce_layered_architecture.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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
public class UserPromotionDto {
    Long id;
    String code;
    String description;
    String discountType;
    BigDecimal discountValue;
    BigDecimal minOrderValue;
    BigDecimal maxDiscount;
    OffsetDateTime expiryDate;
    boolean isActive;
}
