package com.nguyensao.ecommerce_layered_architecture.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.nguyensao.ecommerce_layered_architecture.enums.OrderStatus;
import com.nguyensao.ecommerce_layered_architecture.event.EventType;
import com.nguyensao.ecommerce_layered_architecture.event.domain.OtpEvent;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.InventoryPublisher;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.OtpEventPublisher;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.Order;
import com.nguyensao.ecommerce_layered_architecture.model.OrderItem;
import com.nguyensao.ecommerce_layered_architecture.model.Payment;
import com.nguyensao.ecommerce_layered_architecture.model.Shipping;
import com.nguyensao.ecommerce_layered_architecture.model.User;
import com.nguyensao.ecommerce_layered_architecture.repository.OrderRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.UserRepository;
import com.nguyensao.ecommerce_layered_architecture.utils.GenerateOrder;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OtpEventPublisher otpEventPublisher;

    private final UserRepository userRepository;

    private final InventoryPublisher inventoryPublisher;

    public OrderService(OrderRepository orderRepository, OtpEventPublisher otpEventPublisher,
            UserRepository userRepository, InventoryPublisher inventoryPublisher) {
        this.orderRepository = orderRepository;
        this.otpEventPublisher = otpEventPublisher;
        this.userRepository = userRepository;
        this.inventoryPublisher = inventoryPublisher;
    }

    public Order createOrder(OrderDto orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        String generatedCode = GenerateOrder.generateOrderCode();
        order.setOrderCode(generatedCode);
        Set<OrderItem> orderItems = orderDTO.getItems().stream().map(itemDTO -> {
            OrderItem item = new OrderItem();
            item.setProductId(itemDTO.getProductId());
            item.setColorId(itemDTO.getColorId());
            item.setName(itemDTO.getName());
            item.setPrice(itemDTO.getPrice());
            item.setQuantity(itemDTO.getQuantity());
            item.setColor(itemDTO.getColor());
            item.setStorage(itemDTO.getStorage());
            item.setImageUrl(itemDTO.getImageUrl());

            String skuProduct = String.valueOf(itemDTO.getProductId());
            String skuVariant = itemDTO.getStorage();
            int quantity = itemDTO.getQuantity();
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

            return item;
        }).collect(Collectors.toSet());

        order.setItems(orderItems);

        // Map shipping
        Shipping shipping = new Shipping();
        shipping.setFullName(orderDTO.getShipping().getFullName());
        shipping.setPhone(orderDTO.getShipping().getPhone());
        shipping.setAddressDetail(orderDTO.getShipping().getAddressDetail());
        shipping.setMethod(orderDTO.getShipping().getMethod());
        shipping.setFee(orderDTO.getShipping().getFee());
        order.setShipping(shipping);

        // Map payment
        Payment payment = new Payment();
        payment.setMethod(orderDTO.getPayment().getMethod());
        payment.setStatus(orderDTO.getPayment().getStatus());
        order.setPayment(payment);

        // Map other fields
        order.setPromoCode(orderDTO.getPromoCode());
        order.setDiscount(orderDTO.getDiscount());
        order.setTotal(orderDTO.getTotal());
        order.setNote(orderDTO.getNote());
        order.setOrderStatus(OrderStatus.PENDING);
        Order savedVed = orderRepository.save(order);
        orderDTO.setOrderCode(generatedCode);

        User user = userRepository.findById(orderDTO.getUserId()).orElseThrow();
        OtpEvent otpEvent = OtpEvent.builder()
                .eventType(EventType.CREATE_ORDER)
                .fullName(user.getFullName())
                .email(user.getEmail())
                .otp(null)
                .orderDto(orderDTO)
                .build();
        otpEventPublisher.publishOtpEvent(otpEvent);

        return savedVed;

    }

    public Order createOrderFromVNPay(OrderDto orderDTO, String vnpayTransactionId) {
        Order order = createOrder(orderDTO);
        order.getPayment().setStatus("Đã thanh toán"); // Cập nhật trạng thái thanh toán
        order.setOrderStatus(OrderStatus.PENDING); // Xác nhận đơn hàng
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
    }

    // public List<Order> getAllOrders() {
    // return orderRepository.findAllByOrderByIdDesc();
    // }
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
        return orderRepository.save(order);
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
        // Định nghĩa nhãn cho 12 tháng
        String[] labels = { "T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12" };
        long[] data = new long[12]; // Khởi tạo mảng với 12 tháng, giá trị mặc định là 0

        // Truy vấn doanh thu theo tháng
        List<Object[]> revenueData = orderRepository.findRevenueByMonth(OrderStatus.DELIVERED, year);

        // Điền dữ liệu vào mảng data
        for (Object[] row : revenueData) {
            int month = ((Number) row[0]).intValue(); // MONTH(o.createdAt)
            long totalRevenue = ((Number) row[1]).longValue(); // SUM(o.total)
            data[month - 1] = totalRevenue; // MONTH bắt đầu từ 1, mảng bắt đầu từ 0
        }

        // Tạo Dataset
        RevenueStatsDto.Dataset dataset = new RevenueStatsDto.Dataset();
        dataset.setData(data);

        // Tạo DTO
        RevenueStatsDto statsDto = new RevenueStatsDto();
        statsDto.setLabels(labels);
        statsDto.setDatasets(Arrays.asList(dataset));

        return statsDto;
    }
}