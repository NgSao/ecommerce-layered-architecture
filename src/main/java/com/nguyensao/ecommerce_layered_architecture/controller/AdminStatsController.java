package com.nguyensao.ecommerce_layered_architecture.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.ecommerce_layered_architecture.dto.stats.AdminStatsDto;
import com.nguyensao.ecommerce_layered_architecture.service.AdminStatsService;

@RestController
@RequestMapping("/api/v1")

public class AdminStatsController {
    private final AdminStatsService adminStatsService;

    public AdminStatsController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<AdminStatsDto> getAdminStats() {
        return ResponseEntity.ok().body(adminStatsService.getAdminStats());
    }

}
