package com.nguyensao.ecommerce_layered_architecture.dto.stats;

import lombok.Data;

@Data
public class AdminStatsDto {
    private double totalRevenue;
    private long totalOrders;
    private long pendingOrders;
    private long totalProducts;
    private long lowStockProducts;
    private long totalCustomers;
}