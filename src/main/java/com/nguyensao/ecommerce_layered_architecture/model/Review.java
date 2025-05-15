package com.nguyensao.ecommerce_layered_architecture.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String userId;

    Long productId;

    String imageUrl;

    Integer rating;

    @Column(nullable = false, columnDefinition = "TEXT")
    String comment;

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    List<String> images;

    @ElementCollection
    @CollectionTable(name = "review_replies", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "reply", columnDefinition = "JSON")
    List<Reply> replies;

    Boolean status;

}
