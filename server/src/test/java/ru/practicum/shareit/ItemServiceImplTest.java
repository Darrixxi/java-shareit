package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_shouldSaveAndReturnItem() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@test.com");

        ItemDto dto = new ItemDto(null, "Дрель", "Мощная", true, null, null, null, null);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("Дрель");
        savedItem.setDescription("Мощная");
        savedItem.setAvailable(true);
        savedItem.setOwner(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.create(1L, dto);

        assertNotNull(result);
        assertEquals("Дрель", result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void update_shouldUpdateAndReturnItem() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@test.com");

        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Старая");
        existingItem.setDescription("Старая");
        existingItem.setAvailable(true);
        existingItem.setOwner(owner);

        ItemDto dto = new ItemDto(null, "Новая", "Новая", false, null, null, null, null);

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Новая");
        updatedItem.setDescription("Новая");
        updatedItem.setAvailable(false);
        updatedItem.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.update(1L, 1L, dto);

        assertEquals("Новая", result.getName());
        assertFalse(result.getAvailable());
    }
}