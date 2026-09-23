package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ItemService itemService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void updateItem_shouldReturnOk() throws Exception {
        ItemDto dto = new ItemDto(1L, "Новая", "Новая", false, null, null, null, null);
        when(itemService.update(eq(1L), eq(1L), any())).thenReturn(dto);

        mockMvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItem_shouldReturnOk() throws Exception {
        ItemDto dto = new ItemDto(1L, "Дрель", "Мощная", true, 1L, null, null, null);
        when(itemService.findById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByOwner_shouldReturnOk() throws Exception {
        when(itemService.findAllByOwner(1L)).thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    void search_shouldReturnOk() throws Exception {
        when(itemService.search("дрель", 1L)).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .header(USER_ID_HEADER, 1)
                        .param("text", "дрель"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_shouldReturnCreated() throws Exception {
        String commentBody = "{\"text\":\"Отлично\"}";
        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentBody))
                .andExpect(status().isOk());
    }
}