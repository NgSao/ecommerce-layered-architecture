package com.nguyensao.ecommerce_layered_architecture.controller;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.UpdateOrderStatusRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.SimplifiedPageResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.stats.OrderStatusStatsDto;
import com.nguyensao.ecommerce_layered_architecture.dto.stats.RevenueStatsDto;
import com.nguyensao.ecommerce_layered_architecture.model.Order;
import com.nguyensao.ecommerce_layered_architecture.service.OrderService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/user/create-order")
    public ResponseEntity<Order> createOrder(@RequestBody OrderDto orderDTO) {
        Order order = orderService.createOrder(orderDTO);
        return ResponseEntity.ok().body(order);
    }

    @GetMapping("/user/my-orders")
    public ResponseEntity<List<Order>> getOrdersByToken() {
        List<Order> orders = orderService.getOrdersByToken();
        return ResponseEntity.ok().body(orders);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable String userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok().body(orders);
    }

    @GetMapping("/user/orders/cancel/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled successfully");
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<SimplifiedPageResponse<Order>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id"));
        SimplifiedPageResponse<Order> orderPage = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orderPage);
    }

    @GetMapping("/user/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/orders/status/{id}")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        Order updatedOrder = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/admin/orders/status-stats")
    public ResponseEntity<OrderStatusStatsDto> getStatusStats() {
        return ResponseEntity.ok().body(orderService.getOrderStatusStats());
    }

    @GetMapping("/admin/orders/revenue-stats")
    public ResponseEntity<RevenueStatsDto> getRevenueStats(
            @RequestParam(value = "year", defaultValue = "0") int year) {
        if (year == 0) {
            year = Year.now().getValue();
        }
        return ResponseEntity.ok(orderService.getRevenueStats(year));
    }

}
