package com.nguyensao.ecommerce_layered_architecture.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.constant.ProductConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.BrandAdminDto;
import com.nguyensao.ecommerce_layered_architecture.dto.BrandDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.BrandRequest;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.mapper.BrandMapper;
import com.nguyensao.ecommerce_layered_architecture.model.Brand;
import com.nguyensao.ecommerce_layered_architecture.repository.BrandRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper mapper;
    private final FileService fileService;

    public BrandService(BrandRepository brandRepository, BrandMapper mapper, FileService fileService) {
        this.brandRepository = brandRepository;
        this.mapper = mapper;
        this.fileService = fileService;
    }

    public List<BrandAdminDto> getAllAdminBrands() {
        return brandRepository.findAll().stream()
                .map(mapper::AdminToDto)
                .collect(Collectors.toList());
    }

    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll().stream()
                .map(mapper::brandToDto)
                .collect(Collectors.toList());
    }

    public BrandDto getBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.BRAND_NOT_FOUND));
        return mapper.brandToDto(brand);
    }

    public BrandDto createBrand(BrandRequest request) {
        if (brandRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException(ProductConstant.BRAND_EXISTS);
        }
        Brand brand = mapper.brandToEntity(request);
        brandRepository.save(brand);
        return mapper.brandToDto(brand);
    }

    public BrandDto updateBrand(Long id, BrandRequest request) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.BRAND_NOT_FOUND));
        if (request.getName() != null && brandRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new AppException(ProductConstant.BRAND_EXISTS);
        }
        mapper.brandUpdatedToEntity(existing, request);
        Brand brand = brandRepository.save(existing);
        return mapper.brandToDto(brand);
    }

    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.BRAND_NOT_FOUND));
        brandRepository.deleteById(brand.getId());
    }

    public BrandDto uploadImage(Long id, MultipartFile image) throws IOException {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ProductConstant.BRAND_NOT_FOUND));
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.uploadImage(image);
            brand.setImageUrl(imageUrl);
            brandRepository.save(brand);
        } else {
            throw new AppException("Ảnh không được để trống!");
        }
        return mapper.brandToDto(brand);
    }

}
