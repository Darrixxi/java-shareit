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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void create_unavailableItem_shouldThrowException() {
        User booker = new User(1L, "Ivan", "i@t.ru");
        Item item = new Item(1L, "Дрель", "Мощная", false, booker, null); // available = false!
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, dto));
    }

    @Test
    void approve_byNonOwner_shouldThrowForbiddenException() {
        User owner = new User(1L, "Ivan", "i@t.ru");
        User otherUser = new User(2L, "Petr", "p@t.ru");
        Item item = new Item(1L, "Дрель", "Мощная", true, owner, null);
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, otherUser, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // Пользователь 99 пытается подтвердить (он не владелец)
        assertThrows(ForbiddenException.class, () -> bookingService.approve(99L, 1L, true));
    }
}