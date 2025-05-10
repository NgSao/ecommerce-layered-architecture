package com.nguyensao.ecommerce_layered_architecture.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nguyensao.ecommerce_layered_architecture.dto.AdminPromotionDto;
import com.nguyensao.ecommerce_layered_architecture.dto.UserPromotionDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ApplyCodeRequest;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.Promotion;
import com.nguyensao.ecommerce_layered_architecture.repository.PromotionRepository;

import jakarta.transaction.Transactional;

@Service
public class PromotionService {

    private PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    private UserPromotionDto toUserDTO(Promotion promotion) {
        UserPromotionDto dto = new UserPromotionDto();
        dto.setId(promotion.getId());
        dto.setCode(promotion.getCode());
        dto.setDescription(promotion.getDescription());
        dto.setDiscountType(promotion.getDiscountType());
        dto.setDiscountValue(promotion.getDiscountValue());
        dto.setMinOrderValue(promotion.getMinOrderValue());
        dto.setMaxDiscount(promotion.getMaxDiscount());
        dto.setExpiryDate(promotion.getEndDate());
        dto.setActive(promotion.isActive());
        return dto;
    }

    private AdminPromotionDto toAdminDTO(Promotion promotion) {
        AdminPromotionDto dto = new AdminPromotionDto();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setCode(promotion.getCode());
        dto.setDiscountType(promotion.getDiscountType());
        dto.setDiscountValue(promotion.getDiscountValue());
        dto.setMinOrderValue(promotion.getMinOrderValue());
        dto.setMaxDiscount(promotion.getMaxDiscount());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setActive(promotion.isActive());
        dto.setUsageLimit(promotion.getUsageLimit());
        dto.setUsageCount(promotion.getUsageCount());
        return dto;
    }

    private Promotion toEntity(AdminPromotionDto dto) {
        Promotion promotion = new Promotion();
        promotion.setId(dto.getId());
        promotion.setName(dto.getName());
        promotion.setCode(dto.getCode());
        promotion.setDiscountType(dto.getDiscountType());
        promotion.setDiscountValue(dto.getDiscountValue());
        promotion.setMinOrderValue(dto.getMinOrderValue());
        promotion.setMaxDiscount(dto.getMaxDiscount());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setActive(dto.isActive());
        promotion.setUsageLimit(dto.getUsageLimit());
        promotion.setUsageCount(dto.getUsageCount());
        return promotion;
    }

    public List<UserPromotionDto> getAllActivePromotions() {
        return promotionRepository
                .findByIsActiveTrueAndEndDateAfterOrderByStartDateDesc(OffsetDateTime.now())
                .stream()
                .map(this::toUserDTO)
                .toList();
    }

    public Optional<UserPromotionDto> getPromotionByIdForUser(Long id) {
        return promotionRepository.findById(id).map(this::toUserDTO);
    }

    public Optional<UserPromotionDto> getPromotionByCode(String code, ApplyCodeRequest request) {
        return promotionRepository.findByCode(code)
                .filter(p -> p.isActive() &&
                        p.getEndDate().isAfter(OffsetDateTime.now()) &&
                        p.getUsageCount() < p.getUsageLimit() &&
                        request.getOrderTota().compareTo(p.getMinOrderValue()) >= 0)
                .map(this::toUserDTO);
    }

    public Page<AdminPromotionDto> getAllPromotionsForAdmin(Pageable pageable) {
        return promotionRepository.findAll(pageable)
                .map(this::toAdminDTO);
    }

    public Optional<AdminPromotionDto> getPromotionByIdForAdmin(Long id) {
        return promotionRepository.findById(id)
                .map(this::toAdminDTO);
    }

    @Transactional
    public AdminPromotionDto createPromotion(AdminPromotionDto dto) {
        if (promotionRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Promotion code already exists");
        }
        Promotion promotion = toEntity(dto);
        Promotion saved = promotionRepository.save(promotion);
        return toAdminDTO(saved);
    }

    @Transactional
    public Optional<AdminPromotionDto> updatePromotion(Long id, AdminPromotionDto dto) {
        return promotionRepository.findById(id)
                .map(existing -> {
                    if (!existing.getCode().equals(dto.getCode()) &&
                            promotionRepository.existsByCode(dto.getCode())) {
                        throw new AppException("Promotion code already exists");
                    }

                    existing.setName(dto.getName());
                    existing.setCode(dto.getCode());
                    existing.setDiscountType(dto.getDiscountType());
                    existing.setDiscountValue(dto.getDiscountValue());
                    existing.setMinOrderValue(dto.getMinOrderValue());
                    existing.setMaxDiscount(dto.getMaxDiscount());
                    existing.setStartDate(dto.getStartDate());
                    existing.setEndDate(dto.getEndDate());
                    existing.setActive(dto.isActive());
                    existing.setUsageLimit(dto.getUsageLimit());
                    existing.setUsageCount(dto.getUsageCount());

                    Promotion updated = promotionRepository.save(existing);
                    return toAdminDTO(updated); // Chuyển sang DTO để trả về
                });
    }

    @Transactional
    public boolean deletePromotion(Long id) {
        return promotionRepository.findById(id)
                .map(promotion -> {
                    promotionRepository.delete(promotion);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public Optional<AdminPromotionDto> updatePromotionStatus(Long id, boolean isActive) {
        return promotionRepository.findById(id)
                .map(promotion -> {
                    promotion.setActive(isActive);
                    Promotion updated = promotionRepository.save(promotion);
                    return toAdminDTO(updated);
                });
    }
}