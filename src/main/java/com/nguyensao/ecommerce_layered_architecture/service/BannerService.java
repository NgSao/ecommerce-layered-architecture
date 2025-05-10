package com.nguyensao.ecommerce_layered_architecture.service;

import com.nguyensao.ecommerce_layered_architecture.constant.ProductConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.BannerDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.BannerRequest;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.Banner;
import com.nguyensao.ecommerce_layered_architecture.repository.BannerRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;
    private final FileService fileService;

    public BannerService(BannerRepository bannerRepository, FileService fileService) {
        this.bannerRepository = bannerRepository;
        this.fileService = fileService;
    }

    // Create new banner
    public BannerDto createBanner(BannerRequest request) {
        Banner banner = new Banner();
        banner.setName(request.getName());
        banner.setImageUrl(request.getImageUrl());
        banner.setLink(request.getLink());
        banner.setDisplayOrder(request.getDisplayOrder());

        Banner savedBanner = bannerRepository.save(banner);
        return convertToDto(savedBanner);
    }

    // Update banner
    public BannerDto updateBanner(Long id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new AppException("Banner not found with id: " + id));

        // Update only non-null fields from request
        if (request.getName() != null) {
            banner.setName(request.getName());
        }
        if (request.getImageUrl() != null) {
            banner.setImageUrl(request.getImageUrl());
        }
        if (request.getLink() != null) {
            banner.setLink(request.getLink());
        }
        if (request.getDisplayOrder() != 0) {
            banner.setDisplayOrder(request.getDisplayOrder());
        }

        Banner updatedBanner = bannerRepository.save(banner);
        return convertToDto(updatedBanner);
    }

    // Delete banner
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new AppException("Banner not found with id: " + id));
        bannerRepository.delete(banner);
    }

    // Get all banners
    public List<BannerDto> getAllBanners() {
        return bannerRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get banner by ID
    public BannerDto getBannerById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new AppException("Banner not found with id: " + id));
        return convertToDto(banner);
    }

    // Convert Entity to DTO
    private BannerDto convertToDto(Banner banner) {
        return new BannerDto(
                banner.getId(),
                banner.getName(),
                banner.getImageUrl(),
                banner.getLink(),
                banner.getDisplayOrder());
    }

    public BannerDto uploadImage(Long id, MultipartFile image) throws IOException {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.BRAND_NOT_FOUND));
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.uploadImage(image);
            banner.setImageUrl(imageUrl);
            bannerRepository.save(banner);
        } else {
            throw new AppException("Ảnh không được để trống!");
        }
        return convertToDto(banner);
    }

}