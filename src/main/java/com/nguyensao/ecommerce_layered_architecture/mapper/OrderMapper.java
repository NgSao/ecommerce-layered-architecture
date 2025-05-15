package com.nguyensao.ecommerce_layered_architecture.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
import com.nguyensao.ecommerce_layered_architecture.dto.OrderItemDto;
import com.nguyensao.ecommerce_layered_architecture.dto.PaymentDto;
import com.nguyensao.ecommerce_layered_architecture.dto.ShippingDto;
import com.nguyensao.ecommerce_layered_architecture.model.Order;
import com.nguyensao.ecommerce_layered_architecture.model.OrderItem;
import com.nguyensao.ecommerce_layered_architecture.model.Payment;
import com.nguyensao.ecommerce_layered_architecture.model.Shipping;

@Component
public class OrderMapper {

    public Order orderToEntity(OrderDto orderDto) {
        Order order = new Order();
        order.setUserId(orderDto.getUserId());
        order.setOrderCode(orderDto.getOrderCode());
        order.setPromoCode(orderDto.getPromoCode());
        order.setDiscount(orderDto.getDiscount());
        order.setTotal(orderDto.getTotal());
        order.setNote(orderDto.getNote());
        order.setOrderStatus(orderDto.getOrderStatus());

        Set<OrderItem> orderItems = orderDto.getItems().stream().map(this::mapOrderItemDtoToEntity)
                .collect(Collectors.toSet());
        order.setItems(orderItems);

        Shipping shipping = mapShippingDtoToEntity(orderDto.getShipping());
        order.setShipping(shipping);

        Payment payment = mapPaymentDtoToEntity(orderDto.getPayment());
        order.setPayment(payment);

        return order;
    }

    public OrderDto orderToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(order.getUserId());
        orderDto.setOrderCode(order.getOrderCode());
        orderDto.setPromoCode(order.getPromoCode());
        orderDto.setDiscount(order.getDiscount());
        orderDto.setTotal(order.getTotal());
        orderDto.setNote(order.getNote());
        orderDto.setOrderStatus(order.getOrderStatus());

        Set<OrderItemDto> orderItemDtos = order.getItems().stream().map(this::mapOrderItemEntityToDto)
                .collect(Collectors.toSet());
        orderDto.setItems(orderItemDtos);

        ShippingDto shippingDto = mapShippingEntityToDto(order.getShipping());
        orderDto.setShipping(shippingDto);

        PaymentDto paymentDto = mapPaymentEntityToDto(order.getPayment());
        orderDto.setPayment(paymentDto);

        return orderDto;
    }

    private OrderItem mapOrderItemDtoToEntity(OrderItemDto itemDto) {
        OrderItem item = new OrderItem();
        item.setProductId(itemDto.getProductId());
        item.setColorId(itemDto.getColorId());
        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());
        item.setQuantity(itemDto.getQuantity());
        item.setColor(itemDto.getColor());
        item.setStorage(itemDto.getStorage());
        item.setImageUrl(itemDto.getImageUrl());
        return item;
    }

    private OrderItemDto mapOrderItemEntityToDto(OrderItem item) {
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setProductId(item.getProductId());
        itemDto.setColorId(item.getColorId());
        itemDto.setName(item.getName());
        itemDto.setPrice(item.getPrice());
        itemDto.setQuantity(item.getQuantity());
        itemDto.setColor(item.getColor());
        itemDto.setStorage(item.getStorage());
        itemDto.setImageUrl(item.getImageUrl());
        return itemDto;
    }

    private Shipping mapShippingDtoToEntity(ShippingDto shippingDto) {
        Shipping shipping = new Shipping();
        shipping.setFullName(shippingDto.getFullName());
        shipping.setPhone(shippingDto.getPhone());
        shipping.setAddressDetail(shippingDto.getAddressDetail());
        shipping.setMethod(shippingDto.getMethod());
        shipping.setFee(shippingDto.getFee());
        return shipping;
    }

    private ShippingDto mapShippingEntityToDto(Shipping shipping) {
        ShippingDto shippingDto = new ShippingDto();
        shippingDto.setFullName(shipping.getFullName());
        shippingDto.setPhone(shipping.getPhone());
        shippingDto.setAddressDetail(shipping.getAddressDetail());
        shippingDto.setMethod(shipping.getMethod());
        shippingDto.setFee(shipping.getFee());
        return shippingDto;
    }

    private Payment mapPaymentDtoToEntity(PaymentDto paymentDto) {
        Payment payment = new Payment();
        payment.setMethod(paymentDto.getMethod());
        payment.setStatus(paymentDto.getStatus());
        return payment;
    }

    private PaymentDto mapPaymentEntityToDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setMethod(payment.getMethod());
        paymentDto.setStatus(payment.getStatus());
        return paymentDto;
    }
}