package com.nguyensao.ecommerce_layered_architecture.controller;

import com.nguyensao.ecommerce_layered_architecture.dto.ZaloPayRequestDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ZaloPayCallbackRequest;
import com.nguyensao.ecommerce_layered_architecture.service.ZalopayService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/zalopay")
public class ZaloPayController {

    private final ZalopayService zaloPayService;

    public ZaloPayController(ZalopayService zaloPayService) {
        this.zaloPayService = zaloPayService;
    }

    @PostMapping("/payment")
    public ResponseEntity<?> createPayment(@RequestBody ZaloPayRequestDto request) {
        try {
            if (request.getAmount() == null || request.getAmount() <= 0) {
                return ResponseEntity.badRequest().body("Số tiền không hợp lệ.");
            }

            ZaloPayCallbackRequest response = zaloPayService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi khi tạo thanh toán: " + e.getMessage());
        }
    }

    @GetMapping("/order-status/{appTransId}")
    public ResponseEntity<?> getOrderStatus(@PathVariable String appTransId) {
        try {
            String response = zaloPayService.getOrderStatus(appTransId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi kiểm tra trạng thái đơn hàng: " + e.getMessage());
        }
    }
}
