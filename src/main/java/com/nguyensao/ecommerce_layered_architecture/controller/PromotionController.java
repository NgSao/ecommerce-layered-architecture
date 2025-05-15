package com.nguyensao.ecommerce_layered_architecture.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.AdminPromotionDto;
import com.nguyensao.ecommerce_layered_architecture.dto.UserPromotionDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ApplyCodeRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.UpdateStatusRequest;
import com.nguyensao.ecommerce_layered_architecture.service.PromotionService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class PromotionController {
    private PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/public/promotions")
    public ResponseEntity<List<UserPromotionDto>> getAllPromotions() {
        List<UserPromotionDto> promotions = promotionService.getAllActivePromotions();
        return ResponseEntity.ok().body(promotions);
    }

    @GetMapping("/public/promotions/{id}")
    public ResponseEntity<Optional<UserPromotionDto>> getPromotionById(@PathVariable Long id) {
        return ResponseEntity.ok().body(promotionService.getPromotionByIdForUser(id));
    }

    @PutMapping("/public/promotions/apply/{code}")
    public ResponseEntity<UserPromotionDto> getPromotionByCode(
            @PathVariable String code, @RequestBody ApplyCodeRequest request) {

        return promotionService.getPromotionByCode(code, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/promotions")
    public ResponseEntity<Page<AdminPromotionDto>> getAllPromotionsAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id"));
        Page<AdminPromotionDto> promotions = promotionService.getAllPromotionsForAdmin(pageable);
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/admin/promotions/{id}")
    public ResponseEntity<AdminPromotionDto> getPromotionByIdAdmin(@PathVariable Long id) {
        return promotionService.getPromotionByIdForAdmin(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/promotions/create")
    public ResponseEntity<AdminPromotionDto> createPromotion(@RequestBody AdminPromotionDto dto) {
        AdminPromotionDto created = promotionService.createPromotion(dto);
        return ResponseEntity.ok().body(created);
    }

    @PutMapping("/admin/promotions/updated/{id}")
    public ResponseEntity<Optional<AdminPromotionDto>> updatePromotion(
            @PathVariable Long id,
            @RequestBody AdminPromotionDto dto) {
        return ResponseEntity.ok().body(promotionService.updatePromotion(id, dto));
    }

    @DeleteMapping("/admin/promotions/delete/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        boolean deleted = promotionService.deletePromotion(id);
        return deleted ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/admin/promotions/status/{id}")
    public ResponseEntity<AdminPromotionDto> updatePromotionStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {

        Optional<AdminPromotionDto> updated = promotionService.updatePromotionStatus(id, request.isActive());

        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
