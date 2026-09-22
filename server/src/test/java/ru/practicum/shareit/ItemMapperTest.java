package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    @Test
    void toItemDto_shouldMapFields() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Мощная");
        item.setAvailable(true);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Дрель", dto.getName());
        assertTrue(dto.getAvailable());
    }
}