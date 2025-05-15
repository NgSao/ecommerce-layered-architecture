package com.nguyensao.ecommerce_layered_architecture.controller;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.BannerDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.BannerRequest;
import com.nguyensao.ecommerce_layered_architecture.service.BannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @PostMapping(ApiPathConstant.BANNER_CREATE)
    public ResponseEntity<BannerDto> createBanner(@RequestBody BannerRequest request) {
        return ResponseEntity.ok().body(bannerService.createBanner(request));
    }

    @PutMapping(ApiPathConstant.BANNER_UPDATED)
    public ResponseEntity<BannerDto> updateBanner(@PathVariable Long id, @RequestBody BannerRequest request) {
        return ResponseEntity.ok().body(bannerService.updateBanner(id, request));
    }

    @DeleteMapping(ApiPathConstant.BANNER_DELETE)
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiPathConstant.BANNER_GET_ALL)
    public ResponseEntity<List<BannerDto>> getAllBanners() {
        return ResponseEntity.ok().body(bannerService.getAllBanners());
    }

    @GetMapping(ApiPathConstant.BANNER_GET_ID)
    public ResponseEntity<BannerDto> getBannerById(@PathVariable Long id) {
        return ResponseEntity.ok().body(bannerService.getBannerById(id));
    }

    @PostMapping(value = ApiPathConstant.FILE_UPLOAD_BANNER, consumes = "multipart/form-data")
    public BannerDto uploadImage(
            @PathVariable Long id, @RequestParam("file") MultipartFile image) throws IOException {
        return bannerService.uploadImage(id, image);
    }
}