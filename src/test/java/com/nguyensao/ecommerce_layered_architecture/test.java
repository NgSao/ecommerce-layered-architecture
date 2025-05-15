package com.nguyensao.ecommerce_layered_architecture;

public class test {

}

// package com.nguyensao.ecommerce_layered_architecture.service;

// import com.nguyensao.ecommerce_layered_architecture.dto.OrderDto;
// import org.apache.http.client.methods.CloseableHttpResponse;
// import org.apache.http.client.methods.HttpPost;
// import org.apache.http.entity.StringEntity;
// import org.apache.http.impl.client.CloseableHttpClient;
// import org.apache.http.impl.client.HttpClients;
// import org.json.JSONObject;
// import org.springframework.stereotype.Service;

// import javax.crypto.Mac;
// import javax.crypto.spec.SecretKeySpec;
// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;
// import java.util.Objects;

// @Service
// public class MomoService {

// private static final String PARTNER_CODE = "MOMO";
// private static final String ACCESS_KEY = "F8BBA842ECF85";
// private static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
// private static final String REDIRECT_URL =
// "http://localhost:8080/api/v1/public/momo/callback";
// private static final String IPN_URL = "https://callback.url/notify";
// private static final String REQUEST_TYPE = "payWithMethod";
// private static final String PAYMENT_API_URL =
// "https://test-payment.momo.vn/v2/gateway/api/create";
// private static final String QUERY_API_URL =
// "https://test-payment.momo.vn/v2/gateway/api/query";

// public String createPaymentRequest(String amount, String orderId) {
// validateAmount(amount);

// String requestId = generateRequestId();
// // String orderId = requestId;

// try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
// String rawSignature = buildRawSignature(amount, requestId, orderId);
// String signature = signHmacSHA256(rawSignature, SECRET_KEY);

// JSONObject requestBody = buildPaymentRequestBody(amount, requestId, orderId,
// signature);
// return executeHttpPost(httpClient, PAYMENT_API_URL, requestBody);
// } catch (Exception e) {
// return buildErrorResponse("Failed to create payment request: " +
// e.getMessage());
// }
// }

// public String checkPaymentStatus(String orderId) {
// validateOrderId(orderId);

// String requestId = generateRequestId();

// try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
// String rawSignature = String.format(
// "accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
// ACCESS_KEY, orderId, PARTNER_CODE, requestId);
// String signature = signHmacSHA256(rawSignature, SECRET_KEY);

// JSONObject requestBody = buildStatusRequestBody(requestId, orderId,
// signature);
// return executeHttpPost(httpClient, QUERY_API_URL, requestBody);
// } catch (Exception e) {
// return buildErrorResponse("Failed to check payment status: " +
// e.getMessage());
// }
// }

// public void createOrderFromMomo(OrderDto orderDto, String orderId) {
// // Implement order creation logic here
// // This should interact with OrderService to create or update the order
// // Example: orderService.createOrder(orderDto, orderId);
// }

// private void validateAmount(String amount) {
// Objects.requireNonNull(amount, "Amount cannot be null");
// try {
// if (Long.parseLong(amount) <= 0) {
// throw new IllegalArgumentException("Amount must be positive");
// }
// } catch (NumberFormatException e) {
// throw new IllegalArgumentException("Invalid amount format", e);
// }
// }

// private void validateOrderId(String orderId) {
// Objects.requireNonNull(orderId, "OrderId cannot be null");
// if (orderId.trim().isEmpty()) {
// throw new IllegalArgumentException("OrderId cannot be empty");
// }
// }

// private String generateRequestId() {
// return PARTNER_CODE + System.currentTimeMillis();
// }

// private String buildRawSignature(String amount, String requestId, String
// orderId) {
// return String.format(
// "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
// ACCESS_KEY, amount, "", IPN_URL, orderId, "Thanh toan don hang " + orderId,
// PARTNER_CODE, REDIRECT_URL,
// requestId, REQUEST_TYPE);
// }

// private JSONObject buildPaymentRequestBody(String amount, String requestId,
// String orderId, String signature) {
// JSONObject requestBody = new JSONObject();
// requestBody.put("partnerCode", PARTNER_CODE);
// requestBody.put("accessKey", ACCESS_KEY);
// requestBody.put("requestId", requestId);
// requestBody.put("amount", amount);
// requestBody.put("orderId", orderId);
// requestBody.put("orderInfo", "Thanh toan don hang " + orderId);
// requestBody.put("redirectUrl", REDIRECT_URL);
// requestBody.put("ipnUrl", IPN_URL);
// requestBody.put("extraData", "");
// requestBody.put("requestType", REQUEST_TYPE);
// requestBody.put("signature", signature);
// requestBody.put("lang", "en");
// return requestBody;
// }

// private JSONObject buildStatusRequestBody(String requestId, String orderId,
// String signature) {
// JSONObject requestBody = new JSONObject();
// requestBody.put("partnerCode", PARTNER_CODE);
// requestBody.put("accessKey", ACCESS_KEY);
// requestBody.put("requestId", requestId);
// requestBody.put("orderId", orderId);
// requestBody.put("signature", signature);
// requestBody.put("lang", "en");
// return requestBody;
// }

// private String executeHttpPost(CloseableHttpClient httpClient, String url,
// JSONObject requestBody)
// throws Exception {
// HttpPost httpPost = new HttpPost(url);
// httpPost.setHeader("Content-Type", "application/json");
// httpPost.setEntity(new StringEntity(requestBody.toString(),
// StandardCharsets.UTF_8));

// try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
// BufferedReader reader = new BufferedReader(
// new InputStreamReader(response.getEntity().getContent(),
// StandardCharsets.UTF_8));
// StringBuilder result = new StringBuilder();
// String line;
// while ((line = reader.readLine()) != null) {
// result.append(line);
// }
// String responseString = result.toString();
// return responseString;
// }
// }

// private String signHmacSHA256(String data, String key) throws Exception {
// Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
// SecretKeySpec secretKey = new
// SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
// hmacSHA256.init(secretKey);
// byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));

// StringBuilder hexString = new StringBuilder();
// for (byte b : hash) {
// String hex = Integer.toHexString(0xff & b);
// if (hex.length() == 1) {
// hexString.append('0');
// }
// hexString.append(hex);
// }
// return hexString.toString();
// }

// private String buildErrorResponse(String message) {
// return new JSONObject().put("error", message).toString();
// }
// }
