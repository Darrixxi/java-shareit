package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createUser(UserDto body) {
        return post("/users", body);
    }

    public ResponseEntity<Object> getUser(long userId) {
        return get("/users/{userId}", userId, Map.of("userId", userId));
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("/users");
    }

    public ResponseEntity<Object> updateUser(long userId, UserDto body) {
        return patch("/users/{userId}", userId, Map.of("userId", userId), body);
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        return delete("/users/{userId}", userId, Map.of("userId", userId));
    }
}