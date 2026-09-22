package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void createItem_shouldReturn200() throws Exception {
        ItemDto requestDto = new ItemDto(null, "Дрель", "Мощная", true, null, null, null, null);
        ItemDto responseDto = new ItemDto(1L, "Дрель", "Мощная", true, null, null, null, null);

        when(itemClient.createItem(eq(1L), any(ItemDto.class))).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void searchItems_shouldReturn200() throws Exception {
        List<ItemShortDto> items = List.of(
                new ItemShortDto(1L, "Дрель"),
                new ItemShortDto(2L, "Дрель-перфоратор")
        );

        when(itemClient.searchItems(eq(1L), eq("дрель"))).thenReturn(ResponseEntity.ok(items));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void searchItems_emptyResult_shouldReturnEmptyArray() throws Exception {
        when(itemClient.searchItems(eq(1L), eq("абракадабра"))).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "абракадабра"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}