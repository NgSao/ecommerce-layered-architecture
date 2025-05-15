package com.nguyensao.ecommerce_layered_architecture.controller;

import com.nguyensao.ecommerce_layered_architecture.dto.SePayWebhookDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.SePayRequestDto;
import com.nguyensao.ecommerce_layered_architecture.service.SePayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/public/sepay")
public class SePayController {

    private final SePayService sePayService;

    public SePayController(SePayService sePayService) {
        this.sePayService = sePayService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody SePayRequestDto request) {
        Map<String, String> response = sePayService.createPayment(request);
        if (response.containsKey("message") && response.get("message").startsWith("amount và orderId")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<Map<String, String>> handleCallback(@RequestBody SePayWebhookDto webhookData) {
        Map<String, String> response = sePayService.handleCallback(webhookData);
        if (response.containsKey("message") && response.get("message").startsWith("Dữ liệu webhook không hợp lệ")
                || response.get("message").startsWith("Giao dịch không phải")
                || response.get("message").startsWith("Không tìm thấy orderId")) {
            return ResponseEntity.badRequest().body(response);
        }
        if (response.containsKey("message") && response.get("message").startsWith("Lỗi xử lý webhook")) {
            return ResponseEntity.status(500).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{orderId}")
    public ResponseEntity<Map<String, String>> checkTransaction(
            @PathVariable String orderId) {
        Map<String, String> response = sePayService.checkTransactionAndOrder(orderId);
        if (response.containsKey("message") && response.get("message").startsWith("Không tìm thấy")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}