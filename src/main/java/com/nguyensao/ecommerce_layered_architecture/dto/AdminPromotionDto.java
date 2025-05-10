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
public class AdminPromotionDto {

    Long id;
    String name;
    String code;
    String discountType;
    BigDecimal discountValue;
    BigDecimal minOrderValue;
    BigDecimal maxDiscount;
    OffsetDateTime startDate;
    OffsetDateTime endDate;
    boolean isActive;
    int usageLimit;
    int usageCount;
}
