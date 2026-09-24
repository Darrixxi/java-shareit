package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByRequesterIdNotOrderByCreatedDesc_shouldExcludeOwnRequestsAndSort() {
        User user1 = userRepository.save(new User(null, "User1", "u1@test.ru"));
        User user2 = userRepository.save(new User(null, "User2", "u2@test.ru"));

        ItemRequest req1ByUser1 = requestRepository.save(new ItemRequest(null, "Запрос 1 от User1 (старый)", user1.getId(), LocalDateTime.now().minusDays(2)));
        ItemRequest req1ByUser2 = requestRepository.save(new ItemRequest(null, "Запрос 1 от User2 (новый)", user2.getId(), LocalDateTime.now().minusDays(1)));
        ItemRequest req2ByUser2 = requestRepository.save(new ItemRequest(null, "Запрос 2 от User2 (самый новый)", user2.getId(), LocalDateTime.now()));

        List<ItemRequest> result = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(user1.getId());

        assertEquals(2, result.size());
        assertEquals("Запрос 2 от User2 (самый новый)", result.get(0).getDescription());
        assertEquals("Запрос 1 от User2 (новый)", result.get(1).getDescription());
    }
}