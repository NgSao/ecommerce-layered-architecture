package com.nguyensao.ecommerce_layered_architecture.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.nguyensao.ecommerce_layered_architecture.constant.UserConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.ReviewDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ReplyRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ReviewRequest;
import com.nguyensao.ecommerce_layered_architecture.event.EventType;
import com.nguyensao.ecommerce_layered_architecture.event.domain.FileEvent;
import com.nguyensao.ecommerce_layered_architecture.event.domain.ProductEvent;
import com.nguyensao.ecommerce_layered_architecture.event.publisher.ProductPublisher;
import com.nguyensao.ecommerce_layered_architecture.exception.AppException;
import com.nguyensao.ecommerce_layered_architecture.model.Media;
import com.nguyensao.ecommerce_layered_architecture.model.Product;
import com.nguyensao.ecommerce_layered_architecture.model.Reply;
import com.nguyensao.ecommerce_layered_architecture.model.Review;
import com.nguyensao.ecommerce_layered_architecture.model.User;
import com.nguyensao.ecommerce_layered_architecture.repository.ProductRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.ReviewRepository;
import com.nguyensao.ecommerce_layered_architecture.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductPublisher productPublisher;

    public Map<String, Object> getProductReviews(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId)
                .stream()
                .filter(Review::getStatus)
                .sorted(Comparator.comparingLong(Review::getId).reversed())
                .collect(Collectors.toList());

        List<ReviewDto> reviewDTOs = reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        double average = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        long total = reviews.size();

        Map<Integer, Long> distribution = reviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i, 0L);
        }

        return Map.of(
                "reviews", reviewDTOs,
                "average", average,
                "total", total,
                "distribution", distribution);
    }

    public Review submitReview(ReviewRequest request) {
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new AppException("Rating must be between 1 and 5");
        }
        if (request.getComment().isBlank()) {
            throw new AppException("Comment cannot be empty");
        }

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);

        Optional<Review> existingReview = reviewRepository.findByUserIdAndProductId(uuid, request.getProductId());
        if (existingReview.isPresent()) {
            throw new AppException("You have already reviewed this product.");
        }

        Review review = new Review();
        review.setProductId(request.getProductId());
        review.setUserId(uuid);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setImageUrl(request.getImageUrl());
        review.setStatus(true);
        review.setReplies(new ArrayList<>());
        Review saved = reviewRepository.save(review);

        ProductEvent event = ProductEvent.builder()
                .eventType(EventType.PRODUCT_RATING)
                .productId(saved.getProductId())
                .flagProduct(saved.getRating())
                .build();
        productPublisher.sendProduct(event);

        return saved;
    }

    public Review addReply(ReplyRequest request) {
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new AppException("Review not found"));

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);

        boolean hasReplied = review.getReplies().stream()
                .anyMatch(reply -> reply.getUserId().equals(uuid));
        if (hasReplied) {
            throw new IllegalStateException("You have already replied to this review");
        }
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new AppException("Not found"));

        Reply reply = new Reply();
        reply.setUserId(uuid);
        reply.setFullName(user.getFullName());
        reply.setReply(request.getReply());
        reply.setAdminReply(false);
        reply.setCreatedAt(LocalDateTime.now());

        review.getReplies().add(reply);
        return reviewRepository.save(review);
    }

    public List<Review> getFilteredReviews(String searchQuery, Integer rating, String status, Long productId) {
        List<Review> reviews = reviewRepository.findAll();

        // Apply filters
        if (rating != null && rating > 0) {
            reviews = reviews.stream()
                    .filter(r -> r.getRating().equals(rating))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.equals("all")) {
            reviews = reviews.stream()
                    .filter(r -> status.equals("replied") ? r.getReplies().stream().anyMatch(Reply::isAdminReply)
                            : r.getReplies().stream().noneMatch(Reply::isAdminReply))
                    .collect(Collectors.toList());
        }

        if (productId != null) {
            reviews = reviews.stream()
                    .filter(r -> r.getProductId().equals(productId))
                    .collect(Collectors.toList());
        }

        if (searchQuery != null && !searchQuery.isBlank()) {
            String query = searchQuery.toLowerCase();
            reviews = reviews.stream()
                    .filter(r -> r.getComment().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        // Sort by ID in descending order
        reviews.sort(Comparator.comparing(Review::getId).reversed());

        return reviews;
    }

    public Review addAdminReply(ReplyRequest request) {
        if (request.getReply().isBlank()) {
            throw new IllegalArgumentException("Reply cannot be empty");
        }

        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new AppException("Review not found"));

        // Kiểm tra xem admin đã phản hồi chưa
        // boolean hasAdminReplied = review.getReplies().stream()
        // .anyMatch(reply -> reply.isAdminReply());
        // if (hasAdminReplied) {
        // throw new IllegalStateException("Admin has already replied to this review");
        // }

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uuid = jwt.getClaimAsString(UserConstant.UUID);

        Optional<Reply> existingAdminReply = review.getReplies().stream()
                .filter(Reply::isAdminReply)
                .findFirst();

        if (existingAdminReply.isPresent()) {
            Reply reply = existingAdminReply.get();
            reply.setReply(request.getReply());
            reply.setCreatedAt(LocalDateTime.now());
        } else {
            Reply reply = new Reply();
            reply.setUserId(uuid);
            reply.setFullName("Quản trị viên");
            reply.setReply(request.getReply());
            reply.setAdminReply(true);
            reply.setCreatedAt(LocalDateTime.now());
            review.getReplies().add(reply);
        }
        return reviewRepository.save(review);

    }

    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException("Review not found"));
        reviewRepository.delete(review);
    }

    public List<ReviewDto> getReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .sorted(Comparator.comparingLong(Review::getId).reversed())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void uploadImageReview(FileEvent event) {
        Review review = reviewRepository.findById(event.getProductId())
                .orElseThrow(() -> new AppException("Review not found"));
        review.setImages(event.getImageUrls());
        reviewRepository.save(review);

    }

    private ReviewDto toDto(Review review) {
        User user = userRepository.findById(review.getUserId()).orElseThrow(() -> new AppException("Not id"));
        Product product = productRepository.findById(review.getProductId())
                .orElseThrow(() -> new AppException("Not id"));

        return ReviewDto.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .fullName(user.getFullName())
                .avatarUrl(user.getProfileImageUrl())
                .productId(review.getProductId())
                .productName(product.getName())
                .productImage(product.getImages().stream()
                        .map(Media::getImageUrl)
                        .findFirst()
                        .orElse(null))
                .rating(review.getRating())
                .comment(review.getComment())
                .imageUrl(review.getImageUrl())
                .images(new HashSet<>(review.getImages()))
                .replies(new HashSet<>(review.getReplies()))
                .createAt(review.getCreatedAt())
                .build();
    }

}
