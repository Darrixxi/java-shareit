package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createRequest(long userId, NewItemRequestDto body) {
        return post("/requests", userId, body);
    }

    public ResponseEntity<Object> getUserRequests(long userId) {
        return get("/requests", userId);
    }

    public ResponseEntity<Object> getAllRequests(long userId) {
        return get("/requests/all", userId);
    }

    public ResponseEntity<Object> getRequestById(long userId, long requestId) {
        return get("/requests/{requestId}", userId, Map.of("requestId", requestId));
    }
}