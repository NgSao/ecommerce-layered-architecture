package com.nguyensao.ecommerce_layered_architecture.controller;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.BannerDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.BannerRequest;
import com.nguyensao.ecommerce_layered_architecture.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @PostMapping("/admin/banners/create")
    public ResponseEntity<BannerDto> createBanner(@RequestBody BannerRequest request) {
        BannerDto bannerDto = bannerService.createBanner(request);
        return new ResponseEntity<>(bannerDto, HttpStatus.CREATED);
    }

    @PutMapping("/admin/banners/updated/{id}")
    public ResponseEntity<BannerDto> updateBanner(@PathVariable Long id, @RequestBody BannerRequest request) {
        BannerDto bannerDto = bannerService.updateBanner(id, request);
        return ResponseEntity.ok(bannerDto);
    }

    @DeleteMapping("/admin/banners/delete/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public/banners")
    public ResponseEntity<List<BannerDto>> getAllBanners() {
        List<BannerDto> banners = bannerService.getAllBanners();
        return ResponseEntity.ok(banners);
    }

    @GetMapping("/public/banners/{id}")
    public ResponseEntity<BannerDto> getBannerById(@PathVariable Long id) {
        BannerDto bannerDto = bannerService.getBannerById(id);
        return ResponseEntity.ok(bannerDto);
    }

    @PostMapping(value = "/admin/upload/banners/{id}", consumes = "multipart/form-data")
    public BannerDto uploadImage(
            @PathVariable Long id, @RequestParam("file") MultipartFile image) throws IOException {
        return bannerService.uploadImage(id, image);
    }
}