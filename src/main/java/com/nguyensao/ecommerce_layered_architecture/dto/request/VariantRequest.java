package com.nguyensao.ecommerce_layered_architecture.dto.request;

import java.math.BigDecimal;

import com.nguyensao.ecommerce_layered_architecture.exception.AppException;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class VariantRequest {

    Long id;

    String color;

    String size;

    String imageUrl;

    @Positive(message = "Giá gốc phải lớn hơn 0")
    BigDecimal originalPrice;

    @Positive(message = "Giá bán phải lớn hơn 0")
    BigDecimal salePrice;

    Integer stock;

    Integer displayOrder;

    public void validatePrices() {
        if (salePrice != null && originalPrice != null && salePrice.compareTo(originalPrice) > 0) {
            throw new AppException("Giá bán không được lớn hơn giá gốc");
        }
    }
}
