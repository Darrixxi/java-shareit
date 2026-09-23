package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User createUser(Long id, String name, String email) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        return u;
    }

    private Item createItem(Long id, String name, String desc, Boolean avail, User owner) {
        Item i = new Item();
        i.setId(id);
        i.setName(name);
        i.setDescription(desc);
        i.setAvailable(avail);
        i.setOwner(owner);
        return i;
    }

    @Test
    void create_shouldSaveAndReturnItem() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        ItemDto dto = new ItemDto(null, "Дрель", "Мощная", true, null, null, null, null);
        Item saved = createItem(1L, "Дрель", "Мощная", true, owner);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(saved);
        ItemDto result = itemService.create(1L, dto);
        assertEquals("Дрель", result.getName());
    }

    @Test
    void update_shouldUpdateAndReturnItem() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item existing = createItem(1L, "Old", "Old", true, owner);
        ItemDto dto = new ItemDto(null, "New", "New", false, null, null, null, null);
        Item updated = createItem(1L, "New", "New", false, owner);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenReturn(updated);
        ItemDto result = itemService.update(1L, 1L, dto);
        assertEquals("New", result.getName());
        assertFalse(result.getAvailable());
    }

    @Test
    void getAllByOwner_shouldReturnItems() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", "Мощная", true, owner);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(List.of());
        List<ItemDto> result = itemService.findAllByOwner(1L);
        assertFalse(result.isEmpty());
    }

    @Test
    void search_withEmptyText_shouldReturnEmptyList() {
        List<ItemShortDto> result = itemService.search("", 1L);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).searchByText(anyString());
    }

    @Test
    void search_withValidText_shouldReturnItems() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", "Мощная", true, owner);

        when(itemRepository.searchByText("дрель")).thenReturn(List.of(item));

        List<ItemShortDto> result = itemService.search("дрель", 1L);
        assertFalse(result.isEmpty());
        assertEquals("Дрель", result.get(0).getName());
    }

    @Test
    void addComment_whenBookingApproved_shouldSaveComment() {
        User author = createUser(1L, "Author", "a@t.ru");
        User owner = createUser(2L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", "Мощная", true, owner);

        lenient().when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                any(Long.class), any(Long.class), any(BookingStatus.class), any(LocalDateTime.class)
        )).thenReturn(true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> itemService.addComment(1L, 1L, "Отлично"));
    }

    @Test
    void addComment_whenNoApprovedBooking_shouldThrowValidationException() {
        User author = createUser(1L, "Author", "a@t.ru");
        User owner = createUser(2L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", "Мощная", true, owner);

        lenient().when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                any(Long.class), any(Long.class), any(BookingStatus.class), any(LocalDateTime.class)
        )).thenReturn(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 1L, "Отлично"));
    }

    @Test
    void getById_notFound_shouldThrowException() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.findById(1L, 99L));
    }
}