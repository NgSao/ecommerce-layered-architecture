package com.nguyensao.ecommerce_layered_architecture.controller;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
import com.nguyensao.ecommerce_layered_architecture.dto.request.VNPayCallbackRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.request.VNPayRequestDto;
import com.nguyensao.ecommerce_layered_architecture.service.OrderService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/v1/public/vnpay")
public class VNPayController {

    private OrderService orderService;

    public VNPayController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody VNPayRequestDto request) throws Exception {
        System.out.println("Amount: " + request.getAmount());
        System.out.println("Order ID: " + request.getOrderId());
        if (request.getAmount() == null || request.getOrderId() == null || request.getOrderId().isBlank()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "amount và orderId không được để trống");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf((long) (request.getAmount() * 100))); // VNPay yêu cầu amount * 100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", request.getOrderId());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + request.getOrderId());
        vnp_Params.put("vnp_OrderType", "250000");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", request.getIpAddr() != null ? request.getIpAddr() : "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // Tạo checksum
        StringBuilder hashData = new StringBuilder();
        TreeMap<String, String> sortedParams = new TreeMap<>(vnp_Params);
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            hashData.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)).append("&");
        }
        hashData.deleteCharAt(hashData.length() - 1);
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        // Tạo URL thanh toán
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)).append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)).append("&");
        }
        query.deleteCharAt(query.length() - 1);
        String paymentUrl = vnp_PayUrl + "?" + query;

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<Map<String, String>> handleVNPayCallback(@RequestBody VNPayCallbackRequest request)
            throws Exception {
        Map<String, String> vnpayParams = request.getVnpayParams();
        OrderDto orderDto = request.getOrderDTO();
        // Xác minh checksum
        String vnp_SecureHash = vnpayParams.get("vnp_SecureHash");
        vnpayParams.remove("vnp_SecureHash");

        StringBuilder hashData = new StringBuilder();
        TreeMap<String, String> sortedParams = new TreeMap<>(vnpayParams);
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            hashData.append(entry.getKey()).append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)).append("&");
        }
        hashData.deleteCharAt(hashData.length() - 1);
        String calculatedHash = hmacSHA512(vnp_HashSecret, hashData.toString());

        Map<String, String> response = new HashMap<>();
        if (calculatedHash.equalsIgnoreCase(vnp_SecureHash)) {
            String transactionStatus = vnpayParams.get("vnp_TransactionStatus");
            String orderId = vnpayParams.get("vnp_TxnRef");

            if ("00".equals(transactionStatus)) {
                // Thanh toán thành công, lưu đơn hàng
                orderService.createOrderFromVNPay(orderDto, orderId);
                response.put("status", "success");
                response.put("message", "Thanh toán thành công, đơn hàng đã được tạo");
                response.put("orderId", orderId);
            } else {
                // Thanh toán thất bại
                response.put("status", "failed");
                response.put("message", "Thanh toán không thành công");
            }
        } else {
            // Checksum không hợp lệ
            response.put("status", "failed");
            response.put("message", "Invalid checksum");
        }

        return ResponseEntity.ok(response);
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

// package com.nguyensao.ecommerce_layered_architecture.controller;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.nguyensao.ecommerce_layered_architecture.config.VnpayConfig;
// import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
// import
// com.nguyensao.ecommerce_layered_architecture.dto.request.PaymentRequest;
// import com.nguyensao.ecommerce_layered_architecture.service.OrderService;

// import java.io.UnsupportedEncodingException;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.text.SimpleDateFormat;
// import java.util.*;

// @RestController
// @RequestMapping("/api/v1/public")
// public class PaymentController {

// private OrderService orderService;

// public PaymentController(OrderService orderService) {
// this.orderService = orderService;
// }

// private final String vnp_ReturnUrl =
// "http://localhost:8080/api/v1/public/vnpay/callback";

// @PostMapping("/vnpay/payment")
// public String createPayment(@RequestBody PaymentRequest paymentRequest)
// throws UnsupportedEncodingException {

// String vnp_Version = "2.1.0";
// String vnp_Command = "pay";
// String orderType = "other";

// long amount = 0;
// try {
// amount = Long.parseLong(paymentRequest.getAmount()) * 100;
// } catch (NumberFormatException e) {
// throw new IllegalArgumentException("Số tiền không hợp lệ");
// }

// String bankCode = "NCB";
// String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
// String vnp_IpAddr = "127.0.0.1";
// String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

// Map<String, String> vnp_Params = new HashMap<>();
// vnp_Params.put("vnp_Version", vnp_Version);
// vnp_Params.put("vnp_Command", vnp_Command);
// vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
// vnp_Params.put("vnp_Amount", String.valueOf(amount));
// vnp_Params.put("vnp_CurrCode", "VND");

// vnp_Params.put("vnp_BankCode", bankCode);
// vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
// vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
// vnp_Params.put("vnp_OrderType", orderType);
// vnp_Params.put("vnp_Locale", "vn");
// vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
// vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

// Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
// SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
// String vnp_CreateDate = formatter.format(cld.getTime());
// vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

// cld.add(Calendar.MINUTE, 15);
// String vnp_ExpireDate = formatter.format(cld.getTime());
// vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

// List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
// Collections.sort(fieldNames);
// StringBuilder hashData = new StringBuilder();
// StringBuilder query = new StringBuilder();
// Iterator<String> itr = fieldNames.iterator();
// while (itr.hasNext()) {
// String fieldName = itr.next();
// String fieldValue = vnp_Params.get(fieldName);
// if ((fieldValue != null) && (fieldValue.length() > 0)) {
// hashData.append(fieldName).append('=')
// .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
// query.append(URLEncoder.encode(fieldName,
// StandardCharsets.US_ASCII.toString()))
// .append('=')
// .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
// if (itr.hasNext()) {
// query.append('&');
// hashData.append('&');
// }
// }
// }

// String queryUrl = query.toString();
// String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.secretKey,
// hashData.toString());
// queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
// String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;

// return paymentUrl;
// }

// @GetMapping("/vnpay/callback")
// public ResponseEntity<String> returnPayment(@RequestParam("vnp_ResponseCode")
// String responseCode,
// @RequestBody OrderDto orderDTO) {
// if ("00".equals(responseCode)) {
// orderService.createOrderFromVNPay(orderDTO);
// return ResponseEntity.ok().body("Thanh toán thành công!");
// } else {
// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thanh toán thất
// bại! Mã lỗi: " + responseCode);
// }
// }

// }