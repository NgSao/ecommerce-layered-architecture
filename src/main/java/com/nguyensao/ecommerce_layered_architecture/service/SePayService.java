package com.nguyensao.ecommerce_layered_architecture.service;

import com.nguyensao.ecommerce_layered_architecture.dto.SePayWebhookDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.SePayRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SePayService {

    private static final Logger logger = LoggerFactory.getLogger(SePayService.class);

    private final String sePay_QrUrl = "https://qr.sepay.vn/img";
    private final String sePay_Bank = "OCB";
    private final String sePay_AccountNumber = "0392445255";
    private final String sePay_Template = "compact";

    // Map để lưu trữ transactionId và orderId
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Thời gian TTL là 2 giờ (tính bằng giây)
    private static final long TTL_SECONDS = 2 * 60 * 60;

    public Map<String, String> createPayment(SePayRequestDto request) {
        if (request.getAmount() == null || request.getOrderId() == null || request.getOrderId().isBlank()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "amount và orderId không được để trống");
            return error;
        }

        String accountNumber = request.getVirtualAccount() != null && !request.getVirtualAccount().isBlank()
                ? request.getVirtualAccount()
                : sePay_AccountNumber;

        Map<String, String> qrParams = new HashMap<>();
        qrParams.put("bank", sePay_Bank);
        qrParams.put("acc", accountNumber);
        qrParams.put("template", sePay_Template);
        qrParams.put("amount", String.valueOf(request.getAmount()));
        qrParams.put("des", request.getOrderId());

        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : qrParams.entrySet()) {
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .append("&");
        }
        query.deleteCharAt(query.length() - 1);
        String qrUrl = sePay_QrUrl + "?" + query;

        Map<String, String> response = new HashMap<>();
        response.put("qrUrl", qrUrl);
        return response;
    }

    public Map<String, String> handleCallback(SePayWebhookDto webhookData) {
        try {
            logger.info("Nhận webhook: {}", webhookData);

            if (webhookData.getId() == null || webhookData.getTransferAmount() == null
                    || webhookData.getContent() == null) {
                logger.warn("Dữ liệu webhook không hợp lệ: {}", webhookData);
                Map<String, String> error = new HashMap<>();
                error.put("message", "Dữ liệu webhook không hợp lệ: id, transferAmount hoặc content bị thiếu");
                return error;
            }

            if (!"in".equalsIgnoreCase(webhookData.getTransferType())) {
                logger.warn("Giao dịch không phải loại nhận tiền: transferType = {}", webhookData.getTransferType());
                Map<String, String> error = new HashMap<>();
                error.put("message", "Giao dịch không phải loại nhận tiền (transferType != in)");
                return error;
            }

            String orderId = extractOrderId(webhookData.getContent());
            if (orderId == null) {
                logger.warn("Không tìm thấy orderId trong content: {}", webhookData.getContent());
                Map<String, String> error = new HashMap<>();
                error.put("message", "Không tìm thấy orderId trong content hoặc description");
                return error;
            }

            // Lưu transactionId và orderId vào Map
            String redisKey = "transaction:" + webhookData.getId();
            redisTemplate.opsForValue().set(redisKey, orderId, TTL_SECONDS, TimeUnit.SECONDS);
            logger.info("Đã lưu vào Redis: transactionId = {}, orderId = {}", webhookData.getId(), orderId);
            logger.info("Đã lưu: transactionId = {}, orderId = {}", webhookData.getId(), orderId);

            logger.info("Webhook xử lý thành công cho transactionId = {}, orderId = {}", webhookData.getId(), orderId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Webhook processed successfully");
            response.put("transactionId", webhookData.getId().toString());
            return response;

        } catch (Exception e) {
            logger.error("Lỗi xử lý webhook: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("message", "Lỗi xử lý webhook: " + e.getMessage());
            return error;
        }
    }

    public Map<String, String> checkTransactionAndOrder(String orderId) {
        Map<String, String> response = new HashMap<>();

        String pattern = "transaction:*";
        for (String key : redisTemplate.keys(pattern)) {
            String storedOrderId = redisTemplate.opsForValue().get(key);
            if (orderId.equals(storedOrderId)) {
                String transactionId = key.replace("transaction:", "");
                response.put("message", "Tìm thấy giao dịch");
                response.put("transactionId", transactionId);
                response.put("orderId", orderId);
                return response;
            }
        }

        response.put("message", "Không tìm thấy giao dịch với orderId này");
        return response;
    }

    private String extractOrderId(String content) {
        if (content == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("SN[\\w]+");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group().replace("SN", "");
        }
        return null;
    }
}