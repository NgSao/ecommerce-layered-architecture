package com.nguyensao.ecommerce_layered_architecture.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.constant.ProductConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.CategoryAdminDto;
import com.nguyensao.ecommerce_layered_architecture.dto.CategoryDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.CategoryRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.CategoryResponse;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.Category;
import com.nguyensao.ecommerce_layered_architecture.repository.CategoryRepository;
import com.nguyensao.ecommerce_layered_architecture.utils.SlugUtil;

@Component
public class CategoryMapper {
    private final CategoryRepository categoryRepository;

    public CategoryMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryAdminDto categoryAdminToDto(Category category) {
        CategoryAdminDto dto = new CategoryAdminDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setImage(category.getImageUrl());
        dto.setProductCount(category.getProducts().size());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setParentName(category.getParent() != null ? category.getParent().getName() : null);
        return dto;
    }

    public CategoryResponse categoryResponseToDto(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setName(category.getName());
        return dto;
    }

    public Category categoryToEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(SlugUtil.toSlug(request.getName()));
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        if (request.getParentId() != null) {
            category.setParent(categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ProductConstant.PARENT_CATEGORY_NOT_FOUND)));
        }
        return category;
    }

    public Category categoryUpdatedToEntity(Category category, CategoryRequest request) {
        if (request.getName() != null) {
            category.setName(request.getName());
            category.setSlug(SlugUtil.toSlug(request.getName()));

        }

        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getParentId() != null) {
            category.setParent(categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ProductConstant.PARENT_CATEGORY_NOT_FOUND)));
        }
        return category;
    }

    public CategoryDto categoryToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setImageUrl(category.getImageUrl());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setChildren(category.getChildren().stream()
                .map(this::categoryToDto)
                .collect(Collectors.toSet()));
        return dto;
    }

}
