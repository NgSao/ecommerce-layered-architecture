package com.nguyensao.ecommerce_layered_architecture.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

import java.util.Set;

import com.nguyensao.ecommerce_layered_architecture.enums.OrderStatus;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    Set<OrderItem> items;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_id")
    Shipping shipping;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    Payment payment;

    String promoCode;

    double discount;

    double total;

    String note;

    String orderCode;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;
}
