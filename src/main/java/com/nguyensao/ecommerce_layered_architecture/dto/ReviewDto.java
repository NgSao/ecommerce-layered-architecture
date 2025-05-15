package com.nguyensao.ecommerce_layered_architecture.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

import com.nguyensao.ecommerce_layered_architecture.model.Reply;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ReviewDto {
    Long id;
    String userId;

    String fullName;
    String avatarUrl;
    Long productId;
    String productName;
    String productImage;

    Integer rating;
    String comment;
    String imageUrl;
    Set<String> images;
    Set<Reply> replies;

    Instant createAt;

}