package com.nguyensao.ecommerce_layered_architecture.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.dto.response.BrandResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.CategoryResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.ProductColorResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.ProductResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.VariantResponse;
import com.nguyensao.ecommerce_layered_architecture.model.Category;
import com.nguyensao.ecommerce_layered_architecture.model.Media;
import com.nguyensao.ecommerce_layered_architecture.model.Product;
import com.nguyensao.ecommerce_layered_architecture.model.Variant;

@Component
public class ResponseMapper {
    public ProductResponse productToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        // Basic fields
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setSku(product.getSku());
        response.setDescription(product.getDescription());
        response.setSpecification(product.getSpecification());
        response.setPromotions(product.getPromotions());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setPrice(product.getSalePrice());
        response.setStock(product.getStock());
        response.setSold(product.getSold());
        response.setRating(product.getRating());
        response.setRatingCount(product.getRatingCount());
        response.setCreatedAt(product.getCreatedAt());
        response.setCreatedBy(product.getCreatedBy());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setUpdatedBy(product.getUpdatedBy());

        // Calculate discount
        BigDecimal originalPrice = product.getOriginalPrice();
        BigDecimal salePrice = product.getSalePrice();
        if (originalPrice != null && salePrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = originalPrice.subtract(salePrice)
                    .divide(originalPrice, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setDiscount(discount.intValue());
        } else {
            response.setDiscount(0);
        }

        if (product.getImages() != null) {
            response.setImages(product.getImages().stream()
                    .map(Media::getImageUrl)
                    .collect(Collectors.toSet()));
        }
        if (product.getCategories() != null) {
            response.setCategories(product.getCategories().stream()
                    .map(this::mapToCategoryResponse)
                    .collect(Collectors.toSet()));
        }

        if (product.getBrand() != null) {
            BrandResponse brandDto = new BrandResponse();
            brandDto.setId(product.getBrand().getId());
            brandDto.setName(product.getBrand().getName());
            response.setBrand(brandDto);
        }

        if (product.getVariants() != null) {
            response.setVariants(product.getVariants().stream()
                    .map(this::mapToVariantResponse)
                    .collect(Collectors.toSet()));
        }

        return response;
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        return categoryResponse;
    }

    private VariantResponse mapToVariantResponse(Variant variant) {
        VariantResponse variantResponse = new VariantResponse();
        variantResponse.setId(variant.getId());
        variantResponse.setSku(variant.getSku());
        variantResponse.setColor(variant.getColor());
        variantResponse.setStorage(variant.getSize());
        variantResponse.setPrice(variant.getSalePrice());
        variantResponse.setOriginalPrice(variant.getOriginalPrice());
        variantResponse.setStock(variant.getStockQuantity());
        variantResponse.setImage(variant.getImageUrl());

        // Calculate discount
        BigDecimal originalPrice = variant.getOriginalPrice();
        BigDecimal salePrice = variant.getSalePrice();
        if (originalPrice != null && salePrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = originalPrice.subtract(salePrice)
                    .divide(originalPrice, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            variantResponse.setDiscount(discount.intValue());
        } else {
            variantResponse.setDiscount(0);
        }

        return variantResponse;
    }

    // //
    // public List<ProductColorResponse> productColorToResponses(Product product) {
    // if (product.getVariants() == null || product.getVariants().isEmpty()) {
    // return Collections.singletonList(createProductResponse(product, null));
    // }
    // return product.getVariants().stream()
    // .sorted(Comparator.comparing(Variant::getId)) // Sắp xếp biến thể theo id
    // .map(variant -> createProductResponse(product, variant))
    // .collect(Collectors.toList());
    // }

    public ProductColorResponse createProductResponse(Product product, Variant variant) {
        ProductColorResponse response = new ProductColorResponse();
        response.setDescription(product.getDescription());
        response.setPromotions(product.getPromotions());
        response.setProductId(product.getId());
        if (product.getImages() != null) {
            response.setImages(product.getImages().stream()
                    .map(Media::getImageUrl)
                    .collect(Collectors.toSet()));
        }
        if (product.getCategories() != null) {
            response.setCategories(product.getCategories().stream()
                    .map(this::mapToCategoryResponse)
                    .collect(Collectors.toSet()));
        }
        if (product.getBrand() != null) {
            BrandResponse brandResponse = new BrandResponse();
            brandResponse.setId(product.getBrand().getId());
            brandResponse.setName(product.getBrand().getName());
            response.setBrand(brandResponse);
        }
        response.setRating(product.getRating());
        response.setRatingCount(product.getRatingCount());
        response.setSold(product.getSold());

        if (variant != null) {
            response.setId(variant.getId());
            response.setColorId(variant.getId());
            response.setName(String.format("%s %s %s", product.getName(), variant.getSize(), variant.getColor()));
            response.setColor(variant.getColor());
            response.setStorage(variant.getSize());
            response.setSku(variant.getSku());
            response.setPrice(variant.getSalePrice());
            response.setOriginalPrice(variant.getOriginalPrice());
            response.setStock(variant.getStockQuantity());
            response.setImage(variant.getImageUrl());
            BigDecimal originalPrice = variant.getOriginalPrice();
            BigDecimal salePrice = variant.getSalePrice();
            if (originalPrice != null && salePrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = originalPrice.subtract(salePrice)
                        .divide(originalPrice, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                response.setDiscount(discount.intValue());
            } else {
                response.setDiscount(0);
            }

        } else {
            response.setId(product.getId());
            response.setName(product.getName());
            response.setSku(product.getSku());
            response.setPrice(product.getSalePrice());
            response.setOriginalPrice(product.getOriginalPrice());
            response.setStock(product.getStock());
            BigDecimal originalPrice = product.getOriginalPrice();
            BigDecimal salePrice = product.getSalePrice();
            if (originalPrice != null && salePrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = originalPrice.subtract(salePrice)
                        .divide(originalPrice, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                response.setDiscount(discount.intValue());
            } else {
                response.setDiscount(0);
            }
        }

        return response;
    }

}
