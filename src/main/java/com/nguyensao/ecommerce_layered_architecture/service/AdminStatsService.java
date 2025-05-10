package com.nguyensao.ecommerce_layered_architecture.service;

import com.nguyensao.ecommerce_layered_architecture.dto.stats.AdminStatsDto;
import com.nguyensao.ecommerce_layered_architecture.enums.OrderStatus;
import com.nguyensao.ecommerce_layered_architecture.repository.InventoryRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.OrderRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class AdminStatsService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public AdminStatsService(OrderRepository orderRepository, InventoryRepository inventoryRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    public AdminStatsDto getAdminStats() {
        AdminStatsDto statsDto = new AdminStatsDto();

        // Tổng doanh thu (từ đơn hàng DELIVERED)
        statsDto.setTotalRevenue(orderRepository.sumTotalByOrderStatus(OrderStatus.DELIVERED));

        // Tổng số đơn hàng
        statsDto.setTotalOrders(orderRepository.count());

        // Số đơn hàng PENDING hoặc CONFIRMED
        statsDto.setPendingOrders(
                orderRepository.countByOrderStatusIn(OrderStatus.PENDING));

        // Tổng số sản phẩm (dựa trên inventories)
        statsDto.setTotalProducts(inventoryRepository.count());

        // Số sản phẩm có tồn kho thấp (quantity < 10)
        statsDto.setLowStockProducts(inventoryRepository.countLowStockProducts(10));

        // Tổng số khách hàng
        statsDto.setTotalCustomers(userRepository.count());

        return statsDto;
    }
}