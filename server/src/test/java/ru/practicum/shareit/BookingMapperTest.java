package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {
    @Test
    void toBookingDto_shouldMapFields() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("Booker");

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now());
        booking.setStatus(BookingStatus.WAITING);

        var dto = BookingMapper.toBookingDto(booking);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
    }
}