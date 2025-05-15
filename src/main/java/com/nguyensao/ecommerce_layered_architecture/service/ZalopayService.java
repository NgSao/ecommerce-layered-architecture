package com.nguyensao.ecommerce_layered_architecture.service;

import com.nguyensao.ecommerce_layered_architecture.config.ZaloPayConfig;
import com.nguyensao.ecommerce_layered_architecture.dto.request.ZaloPayCallbackRequest;
import com.nguyensao.ecommerce_layered_architecture.dto.ZaloPayRequestDto;
import com.nguyensao.ecommerce_layered_architecture.utils.HMACUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ZalopayService {

    private String getAppTransId() {
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        int randId = new Random().nextInt(1000000);
        return date + "_" + randId;
    }

    public ZaloPayCallbackRequest createOrder(ZaloPayRequestDto request) throws Exception {
        String appTransId = getAppTransId();
        long appTime = System.currentTimeMillis();
        String appUser = "user123";

        Map<String, Object> order = new HashMap<>();
        order.put("app_id", ZaloPayConfig.config.get("app_id"));
        order.put("app_trans_id", appTransId);
        order.put("app_time", appTime);
        order.put("app_user", appUser);
        order.put("amount", request.getAmount());
        order.put("description", "SN Mobile - Payment for the order #" + appTransId);
        order.put("bank_code", "");
        order.put("item", "[{}]");
        order.put("embed_data", "{}");
        order.put("callback_url", "https://example.com/api/zalopay/callback");

        String data = order.get("app_id") + "|" + appTransId + "|" + appUser + "|" +
                request.getAmount() + "|" + appTime + "|{}|[{}]";
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.config.get("key1"), data);
        order.put("mac", mac);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(ZaloPayConfig.config.get("endpoint"));

            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, Object> entry : order.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }

            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = client.execute(post)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder resultJson = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    resultJson.append(line);
                }

                return new ZaloPayCallbackRequest("Tạo đơn hàng thành công", appTransId, resultJson.toString());
            }
        } catch (Exception e) {
            throw new Exception("Không thể tạo đơn hàng ZaloPay: " + e.getMessage(), e);
        }
    }

    public String getOrderStatus(String appTransId) throws Exception {
        String data = ZaloPayConfig.config.get("app_id") + "|" + appTransId + "|" + ZaloPayConfig.config.get("key1");
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.config.get("key1"), data);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(ZaloPayConfig.config.get("orderstatus"));
            List<NameValuePair> params = Arrays.asList(
                    new BasicNameValuePair("app_id", ZaloPayConfig.config.get("app_id")),
                    new BasicNameValuePair("app_trans_id", appTransId),
                    new BasicNameValuePair("mac", mac));

            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = client.execute(post)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }
        } catch (Exception e) {
            throw new Exception("Không thể lấy trạng thái đơn hàng: " + e.getMessage(), e);
        }
    }
}
