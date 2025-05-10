package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BannerDto {
    Long id;

    String name;

    String imageUrl;

    String link;

    int displayOrder;
}
