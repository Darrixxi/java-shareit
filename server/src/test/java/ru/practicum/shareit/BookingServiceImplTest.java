package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User createUser(Long id, String name, String email) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        return u;
    }

    private Item createItem(Long id, String name, Boolean avail, User owner) {
        Item i = new Item();
        i.setId(id);
        i.setName(name);
        i.setAvailable(avail);
        i.setOwner(owner);
        return i;
    }

    private Booking createBooking(Long id, Item item, User booker, BookingStatus status) {
        Booking b = new Booking();
        b.setId(id);
        b.setItem(item);
        b.setBooker(booker);
        b.setStatus(status);
        b.setStart(LocalDateTime.now());
        b.setEnd(LocalDateTime.now().plusDays(1));
        return b;
    }

    @Test
    void approve_byOwner_shouldUpdateStatus() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        User booker = createUser(2L, "Booker", "b@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        var result = bookingService.approve(1L, 1L, true);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void approve_byNonOwner_shouldThrowForbiddenException() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        User other = createUser(99L, "Other", "x@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        Booking booking = createBooking(1L, item, other, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.approve(99L, 1L, true));
    }

    @Test
    void getAllBookingsByBooker_shouldReturnList() {
        User booker = createUser(1L, "Booker", "b@t.ru");
        User owner = createUser(2L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));

        var result = bookingService.getAllByBooker(1L, "ALL");

        assertFalse(result.isEmpty());
    }
}