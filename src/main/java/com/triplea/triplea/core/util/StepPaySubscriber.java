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
    private final ObjectMapper OM;

    @Value("${step-pay.secret-token}")
    private String secretToken;

    /**
     * step pay 고객 생성 API Request
     * @param user 로그인한 사용자
     * @return Response
     * @throws IOException execute
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
     * @param getCustomer 고객 생성 API 의 Response
     * @param productCode 상품 코드
     * @param priceCode 가격 코드
     * @return Order
     * @throws IOException json
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
     * @param order RequestBody
     * @return Response
     * @throws IOException execute
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
     * @param postOrder 주문 생성 API 의 Response
     * @return String orderCode
     * @throws IOException json
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
     * @param orderCode 주문 코드
     * @return Response
     * @throws IOException execute
     */
    public Response getPaymentLink(String successUrl, String orderCode) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.steppay.kr/api/v1/orders/" + orderCode + "/pay?successUrl=" + successUrl)
                .get()
                .headers(Headers.of("accept", "*/*", "Secret-Token", secretToken))
                .build();
        return CLIENT.newCall(request).execute();
    }

    /**
     * step pay 주문 상세조회 API Request
     * @param orderCode 주문 코드
     * @return Response
     * @throws IOException execute
     */
    public Response getOrder(String orderCode) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.steppay.kr/api/v1/orders/" + orderCode)
                .get()
                .headers(Headers.of("accept", "*/*", "Secret-Token", secretToken))
                .build();
        return CLIENT.newCall(request).execute();
    }

    /**
     * step pay 주문 상세조회 API Response
     * @param getOrder 주문 상세 조회 API 의 Response
     * @return Long subscriptionId
     * @throws IOException json
     */
    public Long getSubscriptionId(Response getOrder) throws IOException {
        if (getOrder.isSuccessful()) {
            String json = getOrder.body() != null ? getOrder.body().string() : "";
            if (!json.isEmpty()) {
                JsonNode rootNode = OM.readTree(json);
                JsonNode subscription = rootNode.path("subscriptions");
                if (!subscription.isEmpty()) return subscription.get(0).path("id").asLong();
                else throw new Exception500("Step Pay 구독 결과가 없습니다");
            } else throw new Exception500("Step Pay 주문 조회 API Response 실패");
        }
        throw new Exception500("Step Pay 주문 조회 API 실패");
    }

    /**
     * step pay 구독 취소 API Request
     * @param subscriptionId 구독 ID
     * @return Response
     * @throws IOException execute
     */
    public Response cancelSubscription(Long subscriptionId) throws IOException {
        RequestBody body = RequestBody.create("{\"whenToCancel\":\"NOW\"}", MEDIATYPE);
        Request request = new Request.Builder()
                .url("https://api.steppay.kr/api/v1/subscriptions/" + subscriptionId + "/cancel")
                .post(body)
                .headers(Headers.of("accept", "*/*", "content-type", MEDIATYPE.type(), "Secret-Token", secretToken))
                .build();

        return CLIENT.newCall(request).execute();
    }

    /**
     * step pay 세션 생성 API Request
     * @param customerId 고객번호
     * @return Response
     * @throws IOException execute
     */
    public Response getSession(Long customerId) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.steppay.kr/api/v1/session/" + customerId)
                .get()
                .headers(Headers.of("accept", "*/*", "Secret-Token", secretToken))
                .build();

        return CLIENT.newCall(request).execute();
    }

    /**
     * step pay 구독 상세조회 API Request, Response: 현재 구독 상태 확인
     * @param subscriptionId 구독 ID
     * @return boolean
     * @throws IOException execute
     */
    public boolean isSubscribe(Long subscriptionId) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.steppay.kr/api/v1/subscriptions/" + subscriptionId)
                .get()
                .headers(Headers.of("accept", "*/*", "Secret-Token", secretToken))
                .build();

        Response response = CLIENT.newCall(request).execute();
        if (response.isSuccessful()) {
            String json = response.body() != null ? response.body().string() : "";
            if (!json.isEmpty()) {
                JsonNode rootNode = OM.readTree(json);
                String status = rootNode.path("status").asText();
                return status.equals("ACTIVE");
            } else throw new Exception500("Step Pay 구독 상세 조회 API Response 실패");
        }
        throw new Exception500("Step Pay 구독 상세 조회 API 실패");
    }
}