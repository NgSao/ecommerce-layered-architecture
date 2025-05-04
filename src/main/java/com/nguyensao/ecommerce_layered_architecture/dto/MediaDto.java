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
public class MediaDto {
    Long id;
    String imageUrl;
    int displayOrder;
    Boolean isPublished;
}
