package com.nguyensao.ecommerce_layered_architecture.dto.stats;

import lombok.Data;

import java.util.List;

@Data
public class RevenueStatsDto {
    private String[] labels;
    private List<Dataset> datasets;

    @Data
    public static class Dataset {
        private long[] data;
    }
}