package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void approveBooking_shouldReturnOk() throws Exception {
        when(bookingService.approve(eq(1L), eq(1L), eq(true))).thenReturn(new BookingDto());

        mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID_HEADER, 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking_shouldReturnOk() throws Exception {
        when(bookingService.getById(1L, 1L)).thenReturn(new BookingDto());

        mockMvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsByBooker_shouldReturnOk() throws Exception {
        when(bookingService.getAllByBooker(eq(1L), any())).thenReturn(List.of());

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsByOwner_shouldReturnOk() throws Exception {
        when(bookingService.getAllByBooker(eq(1L), any())).thenReturn(List.of());

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }
}