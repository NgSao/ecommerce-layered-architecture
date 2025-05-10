package com.nguyensao.ecommerce_layered_architecture.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.CategoryAdminDto;
import com.nguyensao.ecommerce_layered_architecture.dto.CategoryDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.CategoryRequest;
import com.nguyensao.ecommerce_layered_architecture.service.CategoryService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;

    }

    // Category
    @GetMapping(ApiPathConstant.CATEGORY_GET_ALL)
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok().body(categoryService.getAllCategories());
    }

    // Category
    @GetMapping("/admin/categories")
    public ResponseEntity<List<CategoryAdminDto>> getAllAdminCategories() {
        return ResponseEntity.ok().body(categoryService.getAllAdminCategories());
    }

    @GetMapping(ApiPathConstant.CATEGORY_GET_ID)
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok().body(categoryService.getCategory(id));

    }

    @PostMapping(ApiPathConstant.CATEGORY_CREATE)
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok().body(categoryService.createCategory(request));

    }

    @PutMapping(ApiPathConstant.CATEGORY_UPDATED)
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        return ResponseEntity.ok().body(categoryService.updateCategory(id, request));

    }

    @DeleteMapping(ApiPathConstant.CATEGORY_DELETE)
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "Category with id " + id + " deleted successfully";
    }

    @PostMapping(value = ApiPathConstant.FILE_UPLOAD_CATEGORY, consumes = "multipart/form-data")
    public CategoryDto uploadImage(
            @PathVariable Long id, @RequestParam("file") MultipartFile image) throws IOException {
        return categoryService.uploadImage(id, image);
    }

}