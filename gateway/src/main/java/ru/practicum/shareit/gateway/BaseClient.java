package ru.practicum.shareit.gateway;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        return get(path, userId, null);
    }

    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body) {
        return post(path, userId, body, null);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, T body, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body) {
        return patch(path, userId, body, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, T body, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> gatewayResponse;
        try {
            if (parameters != null) {
                gatewayResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                gatewayResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(gatewayResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(org.springframework.http.MediaType.APPLICATION_JSON));
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }
}