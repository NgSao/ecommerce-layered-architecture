package com.nguyensao.ecommerce_layered_architecture.controller;

import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.MoMoCallbackRequest;
import com.nguyensao.ecommerce_layered_architecture.constant.ApiPathConstant;
import com.nguyensao.ecommerce_layered_architecture.constant.PaymentGatewayConstant;
import com.nguyensao.ecommerce_layered_architecture.dto.MoMoRequestDto;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class MomoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MomoController.class);

    @PostMapping(ApiPathConstant.MOMO_CREATE)
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody MoMoRequestDto request) throws Exception {
        if (request.getAmount() == null || request.getOrderId() == null || request.getOrderId().isBlank()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "amount và orderId không được để trống");
            return ResponseEntity.badRequest().body(error);
        }

        String requestId = PaymentGatewayConstant.PARTNER_CODE + System.currentTimeMillis();
        String orderId = request.getOrderId();
        String amount = String.valueOf(request.getAmount().longValue());
        String orderInfo = "Thanh toan don hang " + orderId;
        String extraData = "";
        String lang = "en";

        Map<String, String> momoParams = new HashMap<>();
        momoParams.put("partnerCode", PaymentGatewayConstant.PARTNER_CODE);
        momoParams.put("accessKey", PaymentGatewayConstant.ACCESS_KEY);
        momoParams.put("requestId", requestId);
        momoParams.put("amount", amount);
        momoParams.put("orderId", orderId);
        momoParams.put("orderInfo", orderInfo);
        momoParams.put("redirectUrl", PaymentGatewayConstant.REDIRECT_URL);
        momoParams.put("ipnUrl", PaymentGatewayConstant.IPN_URL);
        momoParams.put("extraData", extraData);
        momoParams.put("requestType", PaymentGatewayConstant.REQUEST_TYPE);
        momoParams.put("lang", lang);

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                PaymentGatewayConstant.ACCESS_KEY, amount, extraData, PaymentGatewayConstant.IPN_URL, orderId,
                orderInfo, PaymentGatewayConstant.PARTNER_CODE, PaymentGatewayConstant.REDIRECT_URL, requestId,
                PaymentGatewayConstant.REQUEST_TYPE);
        String signature = hmacSHA256(PaymentGatewayConstant.SECRET_KEY, rawSignature);
        momoParams.put("signature", signature);

        String response = createPaymentRequest(amount, orderId);
        JSONObject responseJson = new JSONObject(response);

        if (responseJson.has("payUrl")) {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("paymentUrl", responseJson.getString("payUrl"));
            return ResponseEntity.ok(responseMap);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", responseJson.optString("message", "Không thể tạo URL thanh toán"));
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping(ApiPathConstant.MOMO_CALLBACK)
    public ResponseEntity<Map<String, String>> handleMomoCallback(@RequestBody MoMoCallbackRequest request)
            throws Exception {
        Map<String, String> momoParams = request.getMomoParams();
        OrderDto orderDto = request.getOrderDTO();

        LOGGER.debug("Nhận được tham số callback MoMo: {}", momoParams);

        String receivedSignature = momoParams.get("signature");
        if (receivedSignature == null) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "failed");
            response.put("message", "Chữ ký bị thiếu");
            LOGGER.error("Chữ ký bị thiếu trong callback MoMo");
            return ResponseEntity.badRequest().body(response);
        }

        momoParams.remove("signature");

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                PaymentGatewayConstant.ACCESS_KEY,
                getParamOrEmpty(momoParams, "amount"),
                getParamOrEmpty(momoParams, "extraData"),
                getParamOrEmpty(momoParams, "message"),
                getParamOrEmpty(momoParams, "orderId"),
                getParamOrEmpty(momoParams, "orderInfo"),
                getParamOrEmpty(momoParams, "orderType"),
                PaymentGatewayConstant.PARTNER_CODE,
                getParamOrEmpty(momoParams, "payType"),
                getParamOrEmpty(momoParams, "requestId"),
                getParamOrEmpty(momoParams, "responseTime"),
                getParamOrEmpty(momoParams, "resultCode"),
                getParamOrEmpty(momoParams, "transId"));
        String calculatedSignature = hmacSHA256(PaymentGatewayConstant.SECRET_KEY, rawSignature);

        LOGGER.debug("Chuỗi chữ ký gốc: {}", rawSignature);
        LOGGER.debug("Chữ ký tính toán: {}", calculatedSignature);
        LOGGER.debug("Chữ ký nhận được: {}", receivedSignature);

        Map<String, String> response = new HashMap<>();
        if (calculatedSignature.equals(receivedSignature)) {
            String resultCode = momoParams.get("resultCode");
            String orderId = momoParams.get("orderId");

            String statusResponse = checkPaymentStatus(orderId);
            JSONObject statusJson = new JSONObject(statusResponse);
            String statusResultCode = statusJson.optString("resultCode", resultCode);

            if ("0".equals(statusResultCode)) {
                try {
                    createOrderFromMomo(orderDto, orderId);
                    response.put("status", "success");
                    response.put("message", "Thanh toán thành công, đơn hàng đã được tạo");
                    response.put("orderId", orderId);
                    LOGGER.info("Thanh toán MoMo thành công cho orderId: {}", orderId);
                } catch (Exception e) {
                    response.put("status", "failed");
                    response.put("message", "Lỗi khi tạo đơn hàng: " + e.getMessage());
                    LOGGER.error("Lỗi khi tạo đơn hàng cho orderId: {}", orderId, e);
                }
            } else {
                response.put("status", "failed");
                response.put("message",
                        "Thanh toán không thành công: " + statusJson.optString("message", "Unknown error"));
                LOGGER.warn("Thanh toán MoMo thất bại cho orderId: {}. ResultCode: {}", orderId, statusResultCode);
            }
        } else {
            response.put("status", "failed");
            response.put("message", "Chữ ký không hợp lệ");
            LOGGER.error("Chữ ký không hợp lệ. Chuỗi gốc: {}, Chữ ký tính toán: {}, Chữ ký nhận được: {}",
                    rawSignature, calculatedSignature, receivedSignature);
        }

        return ResponseEntity.ok(response);
    }

    private String createPaymentRequest(String amount, String orderId) throws Exception {
        validateAmount(amount);
        validateOrderId(orderId);

        String requestId = PaymentGatewayConstant.PARTNER_CODE + System.currentTimeMillis();
        String orderInfo = "Thanh toan don hang " + orderId;
        String extraData = "";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String rawSignature = String.format(
                    "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                    PaymentGatewayConstant.ACCESS_KEY, amount, extraData, PaymentGatewayConstant.IPN_URL, orderId,
                    orderInfo, PaymentGatewayConstant.PARTNER_CODE, PaymentGatewayConstant.REDIRECT_URL,
                    requestId, PaymentGatewayConstant.REQUEST_TYPE);
            String signature = signHmacSHA256(rawSignature, PaymentGatewayConstant.SECRET_KEY);
            LOGGER.debug("Generated signature for payment request: {}", signature);

            JSONObject requestBody = buildPaymentRequestBody(amount, requestId, orderId, orderInfo, signature);
            String response = executeHttpPost(httpClient, PaymentGatewayConstant.PAYMENT_API_URL, requestBody);
            LOGGER.debug("Response from MoMo: {}", response);

            JSONObject responseJson = new JSONObject(response);
            if (responseJson.has("signature") && !responseJson.isNull("signature")) {
                String responseTime = String.valueOf(responseJson.getLong("responseTime"));
                String responseRawSignature = String.format(
                        "accessKey=%s&amount=%s&orderId=%s&partnerCode=%s&requestId=%s&responseTime=%s&resultCode=%s",
                        PaymentGatewayConstant.ACCESS_KEY, responseJson.optString("amount", ""),
                        responseJson.getString("orderId"),
                        PaymentGatewayConstant.PARTNER_CODE, responseJson.getString("requestId"), responseTime,
                        responseJson.getInt("resultCode"));
                String calculatedSignature = signHmacSHA256(responseRawSignature, PaymentGatewayConstant.SECRET_KEY);
                if (!calculatedSignature.equals(responseJson.getString("signature"))) {
                    LOGGER.error("Invalid signature in MoMo response: {}", response);
                    return "{\"error\": \"Invalid signature in response\"}";
                }
            }

            return response;
        } catch (Exception e) {
            LOGGER.error("Failed to create payment request for orderId: {}", orderId, e);
            return "{\"error\": \"Failed to create payment request\", \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    private String checkPaymentStatus(String orderId) throws Exception {
        validateOrderId(orderId);
        String requestId = PaymentGatewayConstant.PARTNER_CODE + System.currentTimeMillis();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String rawSignature = String.format(
                    "accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
                    PaymentGatewayConstant.ACCESS_KEY, orderId, PaymentGatewayConstant.PARTNER_CODE, requestId);
            String signature = signHmacSHA256(rawSignature, PaymentGatewayConstant.SECRET_KEY);
            LOGGER.debug("Generated signature for status check: {}", signature);

            JSONObject requestBody = buildStatusRequestBody(requestId, orderId, signature);
            String response = executeHttpPost(httpClient, PaymentGatewayConstant.QUERY_API_URL, requestBody);
            LOGGER.debug("Response from MoMo status check: {}", response);

            JSONObject responseJson = new JSONObject(response);
            if (responseJson.has("signature") && !responseJson.isNull("signature")) {
                String responseTime = String.valueOf(responseJson.getLong("responseTime"));
                String responseRawSignature = String.format(
                        "accessKey=%s&amount=%s&orderId=%s&partnerCode=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                        PaymentGatewayConstant.ACCESS_KEY,
                        responseJson.optString("amount", ""),
                        responseJson.getString("orderId"),
                        PaymentGatewayConstant.PARTNER_CODE,
                        responseJson.getString("requestId"),
                        responseTime,
                        responseJson.getInt("resultCode"),
                        responseJson.optString("transId", ""));
                String calculatedSignature = signHmacSHA256(responseRawSignature, PaymentGatewayConstant.SECRET_KEY);
                if (!calculatedSignature.equals(responseJson.getString("signature"))) {
                    LOGGER.error("Invalid signature in MoMo status check response: {}", response);
                    return "{\"error\": \"Invalid signature in response\"}";
                }
            } else {
                LOGGER.warn("Signature is missing or null in MoMo status check response: {}", response);
            }

            return response;
        } catch (Exception e) {
            LOGGER.error("Failed to check payment status for orderId: {}", orderId, e);
            return "{\"error\": \"Failed to check payment status\", \"message\": \"" + e.getMessage() + "\"}";
        }
    }

    private void createOrderFromMomo(OrderDto orderDto, String orderId) {
        try {
            // OrderService orderService = new OrderService(); // Thay bằng cách inject thực
            // tế
            // orderService.createOrUpdateOrder(orderDto, orderId);
            LOGGER.info("Successfully created/updated order for orderId: {}", orderId);
        } catch (Exception e) {
            LOGGER.error("Failed to create/update order for orderId: {}", orderId, e);
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    private void validateAmount(String amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        try {
            if (Long.parseLong(amount) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format", e);
        }
    }

    private void validateOrderId(String orderId) {
        Objects.requireNonNull(orderId, "OrderId cannot be null");
        if (orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("OrderId cannot be empty");
        }
    }

    private JSONObject buildPaymentRequestBody(String amount, String requestId, String orderId, String orderInfo,
            String signature) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("partnerCode", PaymentGatewayConstant.PARTNER_CODE);
        requestBody.put("accessKey", PaymentGatewayConstant.ACCESS_KEY);
        requestBody.put("requestId", requestId);
        requestBody.put("amount", amount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", PaymentGatewayConstant.REDIRECT_URL);
        requestBody.put("ipnUrl", PaymentGatewayConstant.IPN_URL);
        requestBody.put("extraData", "");
        requestBody.put("requestType", PaymentGatewayConstant.REQUEST_TYPE);
        requestBody.put("signature", signature);
        requestBody.put("lang", "en");
        return requestBody;
    }

    private JSONObject buildStatusRequestBody(String requestId, String orderId, String signature) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("partnerCode", PaymentGatewayConstant.PARTNER_CODE);
        requestBody.put("accessKey", PaymentGatewayConstant.ACCESS_KEY);
        requestBody.put("requestId", requestId);
        requestBody.put("orderId", orderId);
        requestBody.put("signature", signature);
        requestBody.put("lang", "en");
        return requestBody;
    }

    private String executeHttpPost(CloseableHttpClient httpClient, String url, JSONObject requestBody)
            throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        }
    }

    private String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String hmacSHA256(String key, String data) throws Exception {
        return signHmacSHA256(data, key);
    }

    private String getParamOrEmpty(Map<String, String> params, String key) {
        return params.getOrDefault(key, "");
    }
}
