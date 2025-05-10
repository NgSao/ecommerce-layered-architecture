package com.nguyensao.ecommerce_layered_architecture.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long productId;
    Long colorId;
    String name;
    double price;
    int quantity;
    String color;
    String storage;
    String imageUrl;
}
