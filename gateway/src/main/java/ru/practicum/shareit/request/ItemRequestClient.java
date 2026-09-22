package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.BaseClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, NewItemRequestDto body) {
        return post("/requests", userId, body);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("/requests", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get("/requests/all", userId);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get("/requests/{requestId}", userId, Map.of("requestId", requestId));
    }
}