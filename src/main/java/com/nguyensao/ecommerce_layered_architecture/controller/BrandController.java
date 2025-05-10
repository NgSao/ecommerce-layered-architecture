package com.nguyensao.ecommerce_layered_architecture.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.BrandAdminDto;
import com.nguyensao.ecommerce_layered_architecture.dto.BrandDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.BrandRequest;
import com.nguyensao.ecommerce_layered_architecture.service.BrandService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;

    }

    @GetMapping("/admin/brands")
    public ResponseEntity<List<BrandAdminDto>> getAllAdminBrands() {
        return ResponseEntity.ok().body(brandService.getAllAdminBrands());
    }

    @GetMapping(ApiPathConstant.BRAND_GET_ALL)
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok().body(brandService.getAllBrands());
    }

    @GetMapping(ApiPathConstant.BRAND_GET_ID)
    public ResponseEntity<BrandDto> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok().body(brandService.getBrand(id));
    }

    @PostMapping(ApiPathConstant.BRAND_CREATE)
    public ResponseEntity<BrandDto> createBrand(@RequestBody BrandRequest request) {
        return ResponseEntity.ok().body(brandService.createBrand(request));
    }

    @PutMapping(ApiPathConstant.BRAND_UPDATED)
    public ResponseEntity<BrandDto> updateBrand(@PathVariable Long id, @RequestBody BrandRequest request) {
        return ResponseEntity.ok().body(brandService.updateBrand(id, request));
    }

    @DeleteMapping(ApiPathConstant.BRAND_DELETE)
    public String deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return "Xóa thành công";
    }

    @PostMapping(value = ApiPathConstant.FILE_UPLOAD_BRAND, consumes = "multipart/form-data")
    public BrandDto uploadImage(
            @PathVariable Long id, @RequestParam("file") MultipartFile image) throws IOException {
        return brandService.uploadImage(id, image);
    }

}