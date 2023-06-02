package com.triplea.triplea.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StepPaySubscriber {
    private final OkHttpClient CLIENT = new OkHttpClient();
    private final MediaType MEDIATYPE = MediaType.parse("application/json");
    private final ObjectMapper OM = new ObjectMapper();

    @Value("${step-pay.secret-token}")
    private String secretToken;

    /**
     * step pay 고객 생성 API Request
     * @param user
     * @return Response
     * @throws IOException
     */
    public Response postCustomer(User user) throws IOException {
        UserRequest.Customer customer = UserRequest.Customer.builder()
                .name(user.getFullName())
                .email(user.getEmail())
                .build();
        RequestBody requestBody = RequestBody.create(OM.writeValueAsString(customer), MEDIATYPE);
        Request request = new Request.Builder()
                .url("https://api.steppay.kr/api/v1/customers")
                .post(requestBody)
                .headers(Headers.of("accept", "*/*", "content-type", MEDIATYPE.type(), "Secret-Token", secretToken))
                .build();
        return CLIENT.newCall(request).execute();
    }

    /**
     * step pay 고객 생성 API Response: customerId, customerCode
     * @param getCustomer
     * @param productCode
     * @param priceCode
     * @return Order
     * @throws IOException
     */
    public UserRequest.Order responseCustomer(Response getCustomer, String productCode, String priceCode) throws IOException {
        if (getCustomer.isSuccessful()) {
            String json = getCustomer.body() != null ? getCustomer.body().string() : "";
            if (!json.isEmpty()) {
                JsonNode rootNode = OM.readTree(json);
                return UserRequest.Order.builder()
                        .customerId(rootNode.path("id").asLong())
                        .customerCode(rootNode.path("code").asText())
                        .items(List.of(UserRequest.Order.Item.builder()
                                .productCode(productCode)
                                .priceCode(priceCode)
                                .build()))
                        .build();
            } else throw new Exception500("Step Pay 고객 생성 API Response 실패");
        }
        throw new Exception500("Step Pay 고객 생성 API 실패");
    }

    /**
     * step pay 주문 생성 API Request
     * @param order
     * @return Response
     * @throws IOException
     */
    public Response postOrder(UserRequest.Order order) throws IOException {
        RequestBody requestBody = RequestBody.create(OM.writeValueAsString(order), MEDIATYPE);
        Request request = new Request.Builder()
                .url("https://api.steppay.kr/api/v1/orders")
                .post(requestBody)
                .headers(Headers.of("accept", "*/*", "content-type", MEDIATYPE.type(), "Secret-Token", secretToken))
                .build();
        return CLIENT.newCall(request).execute();
    }

    /**
     * step pay 주문 생성 API Response
     * @param postOrder
     * @return String orderCode
     * @throws IOException
     */
    public String getOrderCode(Response postOrder) throws IOException {
        if (postOrder.isSuccessful()) {
            String json = postOrder.body() != null ? postOrder.body().string() : "";
            if (!json.isEmpty()) {
                JsonNode rootNode = OM.readTree(json);
                return rootNode.path("orderCode").asText();
            } else throw new Exception500("Step Pay 주문 생성 API Response 실패");
        }
        throw new Exception500("Step Pay 주문 생성 API 실패");
    }

    /**
     * step pay 주문 결제링크 리다이렉트 API Request
     * @param orderCode
     * @return Response
     * @throws IOException
     */
    public Response getPaymentLink(String orderCode) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.steppay.kr/api/v1/orders/" + orderCode + "/pay")
                .get()
                .headers(Headers.of("accept", "*/*", "Secret-Token", secretToken))
                .build();
        return CLIENT.newCall(request).execute();
    }
}
