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

    @PostMapping(ApiPathConstant.CREATE_ORDER)
    public ResponseEntity<Order> createOrder(@RequestBody OrderDto orderDTO) {
        return ResponseEntity.ok().body(orderService.createOrder(orderDTO));
    }

    @GetMapping(ApiPathConstant.MY_ORDERS)
    public ResponseEntity<List<Order>> getOrdersByToken() {
        return ResponseEntity.ok().body(orderService.getOrdersByToken());
    }

    @GetMapping(ApiPathConstant.USER_ORDERS_BY_ID)
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable String userId) {
        return ResponseEntity.ok().body(orderService.getOrdersByUserId(userId));
    }

    @GetMapping(ApiPathConstant.CANCEL_ORDER)
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Order cancelled successfully");
    }

    @GetMapping(ApiPathConstant.GET_ORDER_BY_ID)
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(ApiPathConstant.ORDER_GET)
    public ResponseEntity<SimplifiedPageResponse<Order>> getAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id"));
        SimplifiedPageResponse<Order> orderPage = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orderPage);
    }

    @PutMapping(ApiPathConstant.UPDATE_ORDER_STATUS)
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    @GetMapping(ApiPathConstant.ORDER_STATUS_STATS)
    public ResponseEntity<OrderStatusStatsDto> getStatusStats() {
        return ResponseEntity.ok().body(orderService.getOrderStatusStats());
    }

    @GetMapping(ApiPathConstant.REVENUE_STATS)
    public ResponseEntity<RevenueStatsDto> getRevenueStats(
            @RequestParam(value = "year", defaultValue = "0") int year) {
        if (year == 0) {
            year = Year.now().getValue();
        }
        return ResponseEntity.ok(orderService.getRevenueStats(year));
    }

}
