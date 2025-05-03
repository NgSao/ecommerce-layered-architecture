package com.nguyensao.ecommerce_layered_architecture.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String fullName;
    String phone;
    String city;
    String district;
    String street;
    String addressDetail;
    Boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

}