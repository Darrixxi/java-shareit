package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.gateway.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto body) {
        return post("/users", null, body);
    }

    public ResponseEntity<Object> getUser(Long userId) {
        return get("/users/{userId}", null, Map.of("userId", userId));
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("/users");
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto body) {
        return patch("/users/{userId}", userId, body, Map.of("userId", userId));
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/users/{userId}", userId, Map.of("userId", userId));
    }
}