package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void createRequest_shouldReturnOk() throws Exception {
        Long userId = 1L;
        NewItemRequestDto requestDto = new NewItemRequestDto("Нужна дрель");
        ItemRequestDto responseDto = new ItemRequestDto(1L, "Нужна дрель", LocalDateTime.now(), List.of());

        when(itemRequestClient.createRequest(eq(userId), any(NewItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void getUserRequests_shouldReturnList() throws Exception {
        Long userId = 1L;
        ItemRequestDto request = new ItemRequestDto(1L, "Нужна дрель", LocalDateTime.now(), List.of());

        when(itemRequestClient.getUserRequests(eq(userId)))
                .thenReturn(ResponseEntity.ok(List.of(request)));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

    @Test
    void getAllRequests_shouldReturnList() throws Exception {
        Long userId = 1L;
        ItemRequestDto request = new ItemRequestDto(1L, "Нужен шуруповёрт", LocalDateTime.now(), List.of());

        when(itemRequestClient.getAllRequests(eq(userId)))
                .thenReturn(ResponseEntity.ok(List.of(request)));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestDto request = new ItemRequestDto(1L, "Нужна дрель", LocalDateTime.now(), List.of());

        when(itemRequestClient.getRequestById(eq(userId), eq(requestId)))
                .thenReturn(ResponseEntity.ok(request));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }
}
