package com.nguyensao.ecommerce_layered_architecture.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.persistence.*;

@Entity
@Table(name = "sphipping")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String fullName;
    String phone;
    String addressDetail;
    String method;
    double fee;
}
