package com.nguyensao.ecommerce_layered_architecture.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.nguyensao.ecommerce_layered_architecture.enums.GenderEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.ProviderEnum;
import com.nguyensao.ecommerce_layered_architecture.enums.RoleAuthorities;
import com.nguyensao.ecommerce_layered_architecture.enums.StatusEnum;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String fullName;

    String email;

    String password;

    String phone;

    Instant birthday;

    String profileImageUrl;

    Instant lastLoginDate;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    @Enumerated(EnumType.STRING)
    ProviderEnum provider;

    @Enumerated(EnumType.STRING)
    RoleAuthorities role;

    @Enumerated(EnumType.STRING)
    StatusEnum status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<Provider> providers = new HashSet<>();

}
