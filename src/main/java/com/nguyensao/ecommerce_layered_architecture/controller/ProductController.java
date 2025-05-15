package com.nguyensao.ecommerce_layered_architecture.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.ProductDto;
import com.nguyensao.ecommerce_layered_architecture.dto.VariantDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ProductRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.VariantCreateRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.AdminProductResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.ProductColorResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.ProductResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.response.SimplifiedPageResponse;
import com.nguyensao.ecommerce_layered_architecture.service.ProductService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;

    }

    @GetMapping(ApiPathConstant.PRODUCT_GET_ALL)
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping(ApiPathConstant.PRODUCT_GET_HOT)
    public ResponseEntity<List<ProductResponse>> getAllProductshHot() {
        return ResponseEntity.ok().body(productService.getAllProductsHot());
    }

    @GetMapping(ApiPathConstant.PRODUCT_GET_SALE)
    public ResponseEntity<List<ProductResponse>> getAllProductsSale() {
        return ResponseEntity.ok().body(productService.getSaleProducts());
    }

    @GetMapping(ApiPathConstant.PRODUCT_GET_COLOR)
    public ResponseEntity<SimplifiedPageResponse<ProductColorResponse>> getAllPageProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id"));
        SimplifiedPageResponse<ProductColorResponse> productPage = productService.getAllPageProducts(pageable);
        return ResponseEntity.ok().body(productPage);
    }

    @GetMapping(ApiPathConstant.PRODUCT_GET_ID)
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.getProduct(id));
    }

    @GetMapping(ApiPathConstant.PRODUCT_GET_SEARCH)
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam(value = "query", required = false) String query) {
        List<ProductResponse> products = productService.searchProducts(query);
        return ResponseEntity.ok().body(products);
    }

    @GetMapping(ApiPathConstant.PRODUCT_GET_CATEGORY)
    public ResponseEntity<List<ProductResponse>> getProductCategory(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.getProductsByCategoryId(id));
    }

    @GetMapping(ApiPathConstant.PRODUCT_GET)
    public ResponseEntity<ProductDto> getProductAdmin(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.getProductAdmin(id));
    }

    @PostMapping(ApiPathConstant.PRODUCT_CREATE)
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok().body(productService.createProduct(request));
    }

    @PutMapping(ApiPathConstant.PRODUCT_UPDATED)
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok().body(productService.updateProduct(id, request));
    }

    @DeleteMapping(ApiPathConstant.PRODUCT_DELETE)
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }

    @PostMapping(ApiPathConstant.VARIANT_CREATE)
    public ResponseEntity<List<VariantDto>> addVariants(@RequestBody List<VariantCreateRequest> requests) {
        List<VariantDto> createdVariants = requests.stream()
                .map(productService::addVariant)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(createdVariants);
    }

    // @PutMapping(ApiPathConstant.VARIANT_UPDATED)
    // public ResponseEntity<VariantDto> updateVariant(@PathVariable Long id,
    // @RequestBody VariantRequest request) {
    // return ResponseEntity.ok().body(productService.updateVariant(id, request));
    // }

    @DeleteMapping(ApiPathConstant.VARIANT_DELETE)
    public ResponseEntity<String> deleteVariant(@PathVariable Long id) {
        productService.deleteVariant(id);
        return ResponseEntity.ok().body("Xóa thành công");
    }

    @PostMapping(value = ApiPathConstant.FILE_UPLOAD_PRODUCT, consumes = "multipart/form-data")
    public ProductDto uploadProductImages(
            @PathVariable Long productId, @RequestParam("file") MultipartFile[] images)
            throws IOException {
        return productService.uploadProductImages(productId, images);
    }

    @PostMapping(value = ApiPathConstant.FILE_UPLOAD_VARIANT, consumes = "multipart/form-data")
    public VariantDto uploadVariantImage(
            @PathVariable Long variantId, @RequestParam("file") MultipartFile image) throws IOException {
        return productService.uploadVariantImage(variantId, image);
    }

    @GetMapping("/admin/products/orders")
    public ResponseEntity<List<AdminProductResponse>> getAllProductsOrder(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok().body(productService.getAllProductsOrder(keyword));
    }

}