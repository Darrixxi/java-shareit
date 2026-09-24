package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_andFindAllByOwnerId_shouldReturnItems() {
        User user = new User();
        user.setName("Owner");
        user.setEmail("owner@test.com");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная");
        item.setAvailable(true);
        item.setOwner(user);

        itemRepository.save(item);

        List<Item> items = itemRepository.findAllByOwnerId(user.getId());
        assertTrue(!items.isEmpty());
        assertEquals("Дрель", items.get(0).getName());
    }
}