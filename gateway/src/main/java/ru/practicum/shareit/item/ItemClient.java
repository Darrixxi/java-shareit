package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto body) {
        return post("/items", userId, body);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto body) {
        return patch("/items/{itemId}", userId, body, Map.of("itemId", itemId));
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/items/{itemId}", userId, Map.of("itemId", itemId));
    }

    public ResponseEntity<Object> getAllItems(Long userId) {
        return get("/items", userId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        return get("/items/search?text={text}", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto body) {
        return post("/items/{itemId}/comment", userId, body, Map.of("itemId", itemId));
    }
}