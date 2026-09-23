package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void getUserRequests_shouldReturnOk() throws Exception {
        when(itemRequestService.getUserRequests(1L)).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById_shouldReturnOk() throws Exception {
        when(itemRequestService.getRequestById(1L, 1L)).thenReturn(new ItemRequestDto(1L, "Нужна дрель", LocalDateTime.now(), List.of()));

        mockMvc.perform(get("/requests/1")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk());
    }
}