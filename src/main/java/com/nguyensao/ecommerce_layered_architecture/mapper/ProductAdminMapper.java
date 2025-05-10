package com.nguyensao.ecommerce_layered_architecture.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.dto.response.AdminProductResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.AdminVariantResponse;
import com.nguyensao.ecommerce_layered_architecture.model.Product;
import com.nguyensao.ecommerce_layered_architecture.model.Variant;

@Component
public class ProductAdminMapper {

    public AdminProductResponse productToAdminProductResponse(Product product) {
        if (product == null) {
            return null;
        }

        return AdminProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .image(
                        product.getImages().stream()
                                .findFirst()
                                .map(media -> media.getImageUrl())
                                .orElse(null))
                .originalPrice(product.getOriginalPrice())
                .salePrice(product.getSalePrice())
                .stock(product.getStock())
                .variants(product.getVariants().stream()
                        .map(this::productToAdminVariantResponse)
                        .collect(Collectors.toSet()))
                .build();
    }

    public AdminVariantResponse productToAdminVariantResponse(Variant variant) {
        if (variant == null) {
            return null;
        }

        String size = variant.getSize();
        String color = variant.getColor();

        String name = "";
        if (size != null && !size.isEmpty() && color != null && !color.isEmpty()) {
            name = size + " - " + color;
        } else if (size != null && !size.isEmpty()) {
            name = size;
        } else if (color != null && !color.isEmpty()) {
            name = color;
        }
        return AdminVariantResponse.builder()
                .id(variant.getId())
                .name(name)
                .variant(size)
                .color(color)
                .image(variant.getImageUrl())
                .stock(variant.getStockQuantity())
                .originalPrice(variant.getOriginalPrice())
                .salePrice(variant.getSalePrice())
                .build();
    }

}
