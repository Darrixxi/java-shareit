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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
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
    void create_shouldSaveAndReturnBooking() {
        User booker = createUser(1L, "Booker", "b@t.ru");
        User owner = createUser(2L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        var result = bookingService.create(1L, dto); // или bookItem, если у тебя так
        assertNotNull(result);
    }

    @Test
    void create_itemNotFound_shouldThrowNotFoundException() {
        User booker = createUser(1L, "Booker", "b@t.ru");
        BookingCreateDto dto = new BookingCreateDto(99L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, dto));
    }

    @Test
    void create_invalidDates_shouldThrowValidationException() {
        User booker = createUser(1L, "Booker", "b@t.ru");
        User owner = createUser(2L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, dto));
    }

    @Test
    void approve_byOwner_shouldUpdateStatus() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        User booker = createUser(2L, "Booker", "b@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
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

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(other));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.approve(99L, 1L, true));
    }

    @Test
    void getBooking_notFound_shouldThrowNotFoundException() {
        User user = createUser(1L, "User", "u@t.ru");

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 99L));
    }

    @Test
    void getAllBookingsByBooker_shouldReturnList() {
        User booker = createUser(1L, "Booker", "b@t.ru");
        User owner = createUser(2L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));

        var result = bookingService.getAllByBooker(1L, "ALL");
        assertFalse(result.isEmpty());
    }

    @Test
    void getAllBookingsByOwner_shouldReturnList() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        User booker = createUser(2L, "Booker", "b@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));

        var result = bookingService.getAllByOwner(1L, "ALL");
        assertFalse(result.isEmpty());
    }

    @Test
    void create_itemNotAvailable_shouldThrowValidationException() {
        User booker = createUser(1L, "Booker", "b@t.ru");
        User owner = createUser(2L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", false, owner);
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, dto));
    }

    @Test
    void create_bookingOwnItem_shouldThrowValidationException() {
        User ownerAndBooker = createUser(1L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", true, ownerAndBooker);

        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(ownerAndBooker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, dto));
    }

    @Test
    void approve_alreadyProcessed_shouldThrowValidationException() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        User booker = createUser(2L, "Booker", "b@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.APPROVED);

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void getBooking_byUnauthorizedUser_shouldThrowNotFoundException() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        User booker = createUser(2L, "Booker", "b@t.ru");
        User stranger = createUser(3L, "Stranger", "s@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(stranger));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(3L, 1L));
    }

    @Test
    void create_startEqualsEnd_shouldThrowValidationException() {
        User booker = createUser(1L, "Booker", "b@t.ru");
        User owner = createUser(2L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", true, owner);
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        BookingCreateDto dto = new BookingCreateDto(1L, sameTime, sameTime);

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.create(1L, dto));
    }

    @Test
    void create_userNotFound_shouldThrowNotFoundException() {
        BookingCreateDto dto = new BookingCreateDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, dto));
    }
}