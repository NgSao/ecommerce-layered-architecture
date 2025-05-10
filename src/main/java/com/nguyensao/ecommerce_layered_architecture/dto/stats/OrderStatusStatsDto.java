package com.nguyensao.ecommerce_layered_architecture.dto.stats;

import lombok.Data;

@Data

public class OrderStatusStatsDto {
    private String[] labels;
    private int[] data;
}