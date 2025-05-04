package com.nguyensao.ecommerce_layered_architecture.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.ProductDto;
import com.nguyensao.ecommerce_layered_architecture.dto.VariantDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ProductRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.VariantRequest;
import com.nguyensao.ecommerce_layered_architecture.service.ProductService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;

    }

    @GetMapping(ApiPathConstant.PRODUCT_GET_ALL)
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping(ApiPathConstant.PRODUCT_GET_ID)
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.getProduct(id));
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
    public ResponseEntity<VariantDto> addVariant(@RequestBody VariantRequest request) {
        return ResponseEntity.ok().body(productService.addVariant(request));
    }

    @PutMapping(ApiPathConstant.VARIANT_UPDATED)
    public ResponseEntity<VariantDto> updateVariant(@PathVariable Long id, @RequestBody VariantRequest request) {
        return ResponseEntity.ok().body(productService.updateVariant(id, request));
    }

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

}