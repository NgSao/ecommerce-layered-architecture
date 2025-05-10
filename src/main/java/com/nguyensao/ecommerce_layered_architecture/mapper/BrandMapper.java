package com.nguyensao.ecommerce_layered_architecture.mapper;

import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.dto.BrandAdminDto;
import com.nguyensao.ecommerce_layered_architecture.dto.BrandDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.BrandRequest;
import com.nguyensao.ecommerce_layered_architecture.model.Brand;
import com.nguyensao.ecommerce_layered_architecture.utils.SlugUtil;

@Component
public class BrandMapper {

    public BrandDto brandToDto(Brand brand) {
        BrandDto dto = new BrandDto();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setSlug(brand.getSlug());
        dto.setImageUrl(brand.getImageUrl());
        dto.setDisplayOrder(brand.getDisplayOrder());
        return dto;
    }

    public BrandAdminDto AdminToDto(Brand brand) {
        BrandAdminDto dto = new BrandAdminDto();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setImage(brand.getImageUrl());
        dto.setProductCount(brand.getProducts().size());

        return dto;
    }

    public Brand brandToEntity(BrandRequest request) {
        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setSlug(SlugUtil.toSlug(request.getName()));
        brand.setImageUrl(request.getImageUrl());
        brand.setDisplayOrder(request.getDisplayOrder());
        return brand;
    }

    public Brand brandUpdatedToEntity(Brand brand, BrandRequest request) {
        if (request.getName() != null) {
            brand.setName(request.getName());
            brand.setSlug(SlugUtil.toSlug(request.getName()));
        }
        if (request.getImageUrl() != null) {
            brand.setImageUrl(request.getImageUrl());
        }
        if (request.getDisplayOrder() != null) {
            brand.setDisplayOrder(request.getDisplayOrder());
        }
        return brand;
    }

}
