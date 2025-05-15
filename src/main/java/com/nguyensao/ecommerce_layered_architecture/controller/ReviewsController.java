package com.nguyensao.ecommerce_layered_architecture.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nguyensao.ecommerce_layered_architecture.dto.request.ReplyRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ReviewRequest;
import com.nguyensao.ecommerce_layered_architecture.service.ReviewService;

@RestController
@RequestMapping("/api/v1")
public class ReviewsController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/public/reviews/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok().body(reviewService.getProductReviews(productId));
    }

    // Submit a review
    @PostMapping("/public/reviews/submit")
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok().body(reviewService.submitReview(reviewRequest));
    }

    @PostMapping("/public/reviews/reply")
    public ResponseEntity<?> addReply(@RequestBody ReplyRequest replyRequest) {
        return ResponseEntity.ok().body(reviewService.addReply(replyRequest));
    }

    // Fetch all reviews with filters
    @GetMapping("/admin/reviews/product/filters")
    public ResponseEntity<?> getAllReviews(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long productId) {
        return ResponseEntity.ok().body(reviewService.getFilteredReviews(searchQuery, rating, status, productId));
    }

    @GetMapping("/admin/reviews/products")
    public ResponseEntity<?> getProducts() {
        return ResponseEntity.ok().body(reviewService.getReviews());
    }

    // Admin reply to a review
    @PostMapping("/admin/reviews/reply")
    public ResponseEntity<?> replyToReview(@RequestBody ReplyRequest replyRequest) {
        return ResponseEntity.ok().body(reviewService.addAdminReply(replyRequest));
    }

    // Delete a review
    // @DeleteMapping("/{reviewId}")
    // public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
    // try {
    // adminReviewService.deleteReview(reviewId);
    // return ResponseEntity.ok(Map.of("success", true));
    // } catch (Exception e) {
    // return ResponseEntity.badRequest().body(Map.of("error", "Unable to delete
    // review"));
    // }
    // }

}