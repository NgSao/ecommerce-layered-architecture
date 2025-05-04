package com.nguyensao.ecommerce_layered_architecture.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.constant.ProductConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.CategoryDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.CategoryRequest;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.mapper.CategoryMapper;
import com.nguyensao.ecommerce_layered_architecture.model.Category;
import com.nguyensao.ecommerce_layered_architecture.repository.CategoryRepository;

import java.io.IOException;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FileService fileService;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper,
            FileService fileService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.fileService = fileService;

    }

    public CategoryDto createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ProductConstant.CATEGORY_EXISTS);
        }
        Category category = categoryMapper.categoryToEntity(request);
        categoryRepository.save(category);
        return categoryMapper.categoryToDto(category);
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::categoryToDto)
                .toList();
    }

    public CategoryDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.CATEGORY_NOT_FOUND));
        return categoryMapper.categoryToDto(category);

    }

    public CategoryDto updateCategory(Long id, CategoryRequest request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.CATEGORY_NOT_FOUND));
        if (request.getName() != null && categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new AppException(ProductConstant.CATEGORY_EXISTS);
        }
        categoryMapper.categoryUpdatedToEntity(existing, request);
        Category category = categoryRepository.save(existing);
        return categoryMapper.categoryToDto(category);

    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.CATEGORY_NOT_FOUND));
        deleteRecursively(category);
    }

    private void deleteRecursively(Category category) {
        for (Category child : category.getChildren()) {
            deleteRecursively(child);
        }
        categoryRepository.delete(category);
    }

    public CategoryDto uploadImage(Long id, MultipartFile image) throws IOException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.BRAND_NOT_FOUND));
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.uploadImage(image);
            category.setImageUrl(imageUrl);
            categoryRepository.save(category);
        } else {
            throw new AppException("Ảnh không được để trống!");
        }
        return categoryMapper.categoryToDto(category);
    }

}
