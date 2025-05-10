package com.nguyensao.ecommerce_layered_architecture.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nguyensao.ecommerce_layered_architecture.dto.AddressDto;
import com.nguyensao.ecommerce_layered_architecture.dto.AdminUserAddressDto;
import com.nguyensao.ecommerce_layered_architecture.dto.AdminUserCreateDto;
import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
import com.nguyensao.ecommerce_layered_architecture.dto.OrderItemDto;
import com.nguyensao.ecommerce_layered_architecture.dto.PaymentDto;
import com.nguyensao.ecommerce_layered_architecture.dto.ShippingDto;
import com.nguyensao.ecommerce_layered_architecture.dto.UserAdminDto;
import com.nguyensao.ecommerce_layered_architecture.dto.response.AdminUserDto;
import com.nguyensao.ecommerce_layered_architecture.model.Address;
import com.nguyensao.ecommerce_layered_architecture.model.Order;
import com.nguyensao.ecommerce_layered_architecture.model.OrderItem;
import com.nguyensao.ecommerce_layered_architecture.model.Payment;
import com.nguyensao.ecommerce_layered_architecture.model.Shipping;
import com.nguyensao.ecommerce_layered_architecture.model.User;
import com.nguyensao.ecommerce_layered_architecture.repository.OrderRepository;

@Component
public class UserAdminMapper {

    private final OrderRepository orderRepository;

    public UserAdminMapper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public UserAdminDto userAdminToDto(User user) {
        if (user == null) {
            return null;
        }

        Set<OrderDto> orderDtos = orderRepository.findByUserId(user.getId())
                .stream()
                .map(this::orderToDto)
                .collect(Collectors.toSet());

        int totalOrders = orderDtos.size();
        double totalPrice = orderDtos.stream().mapToDouble(OrderDto::getTotal).sum();

        return UserAdminDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password(user.getPassword())
                .profileImageUrl(user.getProfileImageUrl())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .role(user.getRole())
                .status(user.getStatus())
                .lastLoginDate(user.getLastLoginDate())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .addresses(user.getAddresses() != null
                        ? user.getAddresses().stream()
                                .map(this::addressToDto)
                                .collect(Collectors.toSet())
                        : null)
                .orders(orderDtos)
                .totalOrders(totalOrders)
                .totalPrice((int) totalPrice) // Cast to int as per UserDto
                .build();
    }

    private OrderDto orderToDto(Order order) {
        if (order == null) {
            return null;
        }
        return OrderDto.builder()
                .userId(order.getUserId())
                .items(order.getItems() != null
                        ? order.getItems().stream()
                                .map(this::orderItemToDto)
                                .collect(Collectors.toSet())
                        : null)
                .shipping(order.getShipping() != null
                        ? shippingToDto(order.getShipping())
                        : null)
                .payment(order.getPayment() != null
                        ? paymentToDto(order.getPayment())
                        : null)
                .promoCode(order.getPromoCode())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderCode(order.getOrderCode())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .note(order.getNote())
                .orderStatus(order.getOrderStatus())
                .build();
    }

    private OrderItemDto orderItemToDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        return OrderItemDto.builder()
                .productId(orderItem.getProductId())
                .colorId(orderItem.getColorId())
                .name(orderItem.getName())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .color(orderItem.getColor())
                .storage(orderItem.getStorage())
                .imageUrl(orderItem.getImageUrl())
                .build();
    }

    private ShippingDto shippingToDto(Shipping shipping) {
        if (shipping == null) {
            return null;
        }
        return ShippingDto.builder()
                .fullName(shipping.getFullName())
                .phone(shipping.getPhone())
                .addressDetail(shipping.getAddressDetail())
                .method(shipping.getMethod())
                .fee(shipping.getFee())
                .build();
    }

    private PaymentDto paymentToDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        return PaymentDto.builder()
                .method(payment.getMethod())
                .status(payment.getStatus())
                .build();
    }

    private AddressDto addressToDto(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDto.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .addressDetail(address.getAddressDetail())
                .active(address.getActive())
                .build();
    }

    public User dtoAdminToUser(AdminUserCreateDto adminUserCreateDto) {
        if (adminUserCreateDto == null) {
            return null;
        }

        return User.builder()
                .fullName(adminUserCreateDto.getFullName())
                .email(adminUserCreateDto.getEmail())
                .phone(adminUserCreateDto.getPhone())
                .password(adminUserCreateDto.getPassword())
                .addresses(adminUserCreateDto.getAddress() != null
                        ? adminUserCreateDto.getAddress().stream()
                                .map(this::dtoToAddress)
                                .collect(Collectors.toSet())
                        : null)
                .build();
    }

    private Address dtoToAddress(AdminUserAddressDto adminUserAddressDto) {

        if (adminUserAddressDto == null) {
            return null;
        }
        return Address.builder()
                .fullName(adminUserAddressDto.getFullName())
                .phone(adminUserAddressDto.getPhone())
                .city(adminUserAddressDto.getCity())
                .district(adminUserAddressDto.getDistrict())
                .street(adminUserAddressDto.getStreet())
                .addressDetail(adminUserAddressDto.getAddressDetail())
                .active(true)
                .build();
    }

    public AdminUserDto adminToDtoUser(User user) {
        if (user == null) {
            return null;
        }
        return AdminUserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddresses() != null
                        ? user.getAddresses().stream()
                                .map(this::addressToDto)
                                .collect(Collectors.toSet())
                        : null)
                .build();
    }

}