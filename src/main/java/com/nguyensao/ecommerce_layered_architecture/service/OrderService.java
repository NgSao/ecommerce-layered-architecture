package com.nguyensao.ecommerce_layered_architecture.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.nguyensao.ecommerce_layered_architecture.constant.UserConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.UpdateOrderStatusRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.response.SimplifiedPageResponse;
import com.nguyensao.ecommerce_layered_architecture.dto.stats.OrderStatusStatsDto;
import com.nguyensao.ecommerce_layered_architecture.dto.stats.RevenueStatsDto;
import com.nguyensao.ecommerce_layered_architecture.enums.NotificationEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.OrderStatus;
import com.nguyensao.ecommerce_layered_architecture.event.EventType;
import com.nguyensao.ecommerce_layered_architecture.event.domain.NotificationEvent;
import com.nguyensao.ecommerce_layered_architecture.event.domain.OrderEvent;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.InventoryPublisher;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.NotificationPublisher;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.OrderEventPublisher;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.mapper.OrderMapper;
import com.nguyensao.ecommerce_layered_architecture.model.Order;
import com.nguyensao.ecommerce_layered_architecture.model.User;
import com.nguyensao.ecommerce_layered_architecture.repository.OrderRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.UserRepository;
import com.nguyensao.ecommerce_layered_architecture.utils.GenerateOrder;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final UserRepository userRepository;
    private final InventoryPublisher inventoryPublisher;
    private final NotificationPublisher notificationPublisher;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher orderEventPublisher,
            UserRepository userRepository, InventoryPublisher inventoryPublisher,
            NotificationPublisher notificationPublisher, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
        this.userRepository = userRepository;
        this.inventoryPublisher = inventoryPublisher;
        this.notificationPublisher = notificationPublisher;
        this.orderMapper = orderMapper;
    }

    public Order createOrder(OrderDto orderDto) {
        Order order = orderMapper.orderToEntity(orderDto);
        String generatedCode = GenerateOrder.generateOrderCode();
        order.setOrderCode(generatedCode);
        order.setOrderStatus(OrderStatus.PENDING);
        order.getItems().forEach(item -> {
            String skuProduct = String.valueOf(item.getProductId());
            String skuVariant = item.getStorage();
            int quantity = item.getQuantity();
            inventoryPublisher.publishInventoryEvent(
                    EventType.ORDER_INVENTORY,
                    skuProduct,
                    skuVariant,
                    quantity);
            inventoryPublisher.publishProductEvent(
                    EventType.PRODUCT_INVENTORY,
                    skuProduct,
                    skuVariant,
                    quantity);
        });
        orderDto.setOrderCode(generatedCode);
        Order savedOrder = orderRepository.save(order);

        User user = userRepository.findById(orderDto.getUserId())
                .orElseThrow(() -> new AppException("User not found"));
        OrderEvent orderEvent = OrderEvent.builder()
                .eventType(EventType.KAFKA_ORDER)
                .email(user.getEmail())
                .orderCode(savedOrder.getOrderCode())
                .orderStatus(savedOrder.getOrderStatus())
                .orderDto(orderDto)
                .build();
        orderEventPublisher.publishOrderEvent(orderEvent);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .eventType(EventType.ORDER_NOTIFICATION)
                .userId(orderDto.getUserId())
                .type(NotificationEnum.ORDER)
                .flag("PENDING")
                .data(generatedCode)
                .flagId(savedOrder.getId())
                .build();
        notificationPublisher.sendNotification(notificationEvent);

        return savedOrder;
    }

    public Order createOrderFromVNPay(OrderDto orderDto, String vnpayTransactionId) {
        Order order = createOrder(orderDto);
        order.getPayment().setStatus("Đã thanh toán");
        order.setOrderStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByToken() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getClaimAsString(UserConstant.UUID);
        return orderRepository.findByUserIdOrderByIdDesc(userId);
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found"));
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new AppException("Only PENDING orders can be cancelled");
        }
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getClaimAsString(UserConstant.UUID);
        if (!order.getUserId().equals(userId)) {
            throw new AppException("You can only cancel your own orders");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .eventType(EventType.ORDER_NOTIFICATION)
                .userId(order.getUserId())
                .type(NotificationEnum.ORDER)
                .flag("CANCELLED")
                .flagData("CUSTOMER")
                .data(order.getOrderCode())
                .flagId(order.getId())
                .build();
        notificationPublisher.sendNotification(notificationEvent);

        OrderDto orderDto = orderMapper.orderToDto(order);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found"));

        OrderEvent orderEvent = OrderEvent.builder()
                .eventType(EventType.KAFKA_ORDER)
                .email(user.getEmail())
                .orderCode(order.getOrderCode())
                .orderStatus(order.getOrderStatus())
                .orderDto(orderDto)
                .flag(false)
                .build();
        orderEventPublisher.publishOrderEvent(orderEvent);
    }

    public SimplifiedPageResponse<Order> getAllOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByOrderByIdDesc(pageable);
        return new SimplifiedPageResponse<>(orderPage);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found"));
        order.setOrderStatus(request.getStatus());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .eventType(EventType.ORDER_NOTIFICATION)
                .userId(order.getUserId())
                .type(NotificationEnum.ORDER)
                .flag(request.getStatus().name())
                .flagId(order.getId())
                .data(order.getOrderCode())
                .build();
        notificationPublisher.sendNotification(notificationEvent);

        Order savedOrder = orderRepository.save(order);
        OrderDto orderDto = orderMapper.orderToDto(savedOrder);

        User user = userRepository.findById(savedOrder.getUserId())
                .orElseThrow(() -> new AppException("User not found"));

        OrderEvent orderEvent = OrderEvent.builder()
                .eventType(EventType.KAFKA_ORDER)
                .email(user.getEmail())
                .orderCode(order.getOrderCode())
                .orderStatus(order.getOrderStatus())
                .orderDto(orderDto)
                .flag(true)
                .build();
        orderEventPublisher.publishOrderEvent(orderEvent);

        return savedOrder;
    }

    public OrderStatusStatsDto getOrderStatusStats() {
        String[] labels = { "Đang xử lý", "Đang giao", "Đã giao", "Đã hủy" };
        int[] data = new int[labels.length];

        data[0] = (int) (orderRepository.countByOrderStatus(OrderStatus.PENDING) +
                orderRepository.countByOrderStatus(OrderStatus.CONFIRMED));
        data[1] = (int) orderRepository.countByOrderStatus(OrderStatus.SHIPPED);
        data[2] = (int) orderRepository.countByOrderStatus(OrderStatus.DELIVERED);
        data[3] = (int) orderRepository.countByOrderStatus(OrderStatus.CANCELLED);

        OrderStatusStatsDto statsDto = new OrderStatusStatsDto();
        statsDto.setLabels(labels);
        statsDto.setData(data);

        return statsDto;
    }

    public RevenueStatsDto getRevenueStats(int year) {
        String[] labels = { "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12" };
        long[] data = new long[12];

        List<Object[]> revenueData = orderRepository.findRevenueByMonth(OrderStatus.DELIVERED, year);

        for (Object[] row : revenueData) {
            int month = ((Number) row[0]).intValue();
            long totalRevenue = ((Number) row[1]).longValue();
            data[month - 1] = totalRevenue;
        }

        RevenueStatsDto.Dataset dataset = new RevenueStatsDto.Dataset();
        dataset.setData(data);

        RevenueStatsDto statsDto = new RevenueStatsDto();
        statsDto.setLabels(labels);
        statsDto.setDatasets(Arrays.asList(dataset));

        return statsDto;
    }

    public Order findByOrderCodeId(String orderId) {
        return orderRepository.findByOrderCode(orderId)
                .orElseThrow(() -> new AppException("Order not found"));
    }
}