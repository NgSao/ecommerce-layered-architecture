package com.nguyensao.ecommerce_layered_architecture.dto.request;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewRequest {
    Long productId;

    String imageUrl;

    Integer rating;
    String comment;
    List<String> images;

}
