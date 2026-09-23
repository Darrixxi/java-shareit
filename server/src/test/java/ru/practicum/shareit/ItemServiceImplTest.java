package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

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
    @Mock
    private ru.practicum.shareit.request.ItemRequestRepository requestRepository;

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
    void create_withRequestId_shouldSaveAndReturnItem() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        ItemDto dto = new ItemDto(null, "Дрель", "Мощная", true, 5L, null, null, null);
        Item saved = createItem(1L, "Дрель", "Мощная", true, owner);
        saved.setRequestId(5L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(5L)).thenReturn(Optional.of(new ItemRequest()));
        when(itemRepository.save(any(Item.class))).thenReturn(saved);

        ItemDto result = itemService.create(1L, dto);
        assertEquals("Дрель", result.getName());
    }

    @Test
    void create_invalidItem_shouldThrowValidationException() {
        ItemDto dto = new ItemDto(null, " ", "Мощная", true, null, null, null, null);
        assertThrows(ValidationException.class, () -> itemService.create(1L, dto));
    }

    @Test
    void update_shouldUpdateAllFields() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item existing = createItem(1L, "Old", "Old", true, owner);
        ItemDto dto = new ItemDto(null, "New", "NewDesc", false, null, null, null, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));
        ItemDto result = itemService.update(1L, 1L, dto);
        assertEquals("New", result.getName());
        assertFalse(result.getAvailable());
    }

    @Test
    void update_wrongOwner_shouldThrowNotFoundException() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item existing = createItem(1L, "Old", "Old", true, owner);
        ItemDto dto = new ItemDto(null, "New", "New", false, null, null, null, null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        assertThrows(NotFoundException.class, () -> itemService.update(2L, 1L, dto));
    }

    @Test
    void findAllByOwner_empty_shouldReturnEmptyList() {
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of());
        List<ItemDto> result = itemService.findAllByOwner(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByOwner_shouldReturnItems() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", "Мощная", true, owner);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(List.of());
        List<ItemDto> result = itemService.findAllByOwner(1L);
        assertFalse(result.isEmpty());
    }

    @Test
    void search_withNullText_shouldReturnEmptyList() {
        List<ItemShortDto> result = itemService.search(null, 1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void search_withValidText_shouldReturnItems() {
        Item item = createItem(1L, "Дрель", "Мощная", true, createUser(1L, "O", "o@t.ru"));
        when(itemRepository.searchByText("дрель")).thenReturn(List.of(item));
        List<ItemShortDto> result = itemService.search("дрель", 1L);
        assertFalse(result.isEmpty());
    }

    @Test
    void findById_shouldReturnItemWithBookingsAndComments() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        User author = createUser(2L, "Author", "a@t.ru");
        Item item = createItem(1L, "Дрель", "Мощная", true, owner);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Good");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment));

        when(bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(eq(1L), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStartGreaterThanEqualOrderByStartAsc(eq(1L), any()))
                .thenReturn(Optional.empty());

        ItemDto result = itemService.findById(1L, 1L);
        assertNotNull(result);
        assertEquals(1, result.getComments().size());
    }

    @Test
    void addComment_whenBookingApproved_shouldSaveComment() {
        User author = createUser(1L, "Author", "a@t.ru");
        Item item = createItem(1L, "Дрель", "Мощная", true, createUser(2L, "O", "o@t.ru"));
        lenient().when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(any(), any(), any(), any())).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> {
            Comment c = i.getArgument(0);
            c.setId(1L);
            return c;
        });
        assertDoesNotThrow(() -> itemService.addComment(1L, 1L, "Отлично"));
    }

    @Test
    void addComment_whenNoApprovedBooking_shouldThrowValidationException() {
        Item item = createItem(1L, "Дрель", "Мощная", true, createUser(2L, "O", "o@t.ru"));
        lenient().when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(any(), any(), any(), any())).thenReturn(false);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L, "A", "a@t.ru")));
        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 1L, "Отлично"));
    }

    @Test
    void findById_notFound_shouldThrowException() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.findById(1L, 99L));
    }

    @Test
    void update_shouldUpdateOnlyDescription() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item existing = createItem(1L, "Дрель", "Старое описание", true, owner);
        ItemDto dto = new ItemDto(null, null, "Новое описание", null, null, null, null, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.update(1L, 1L, dto);
        assertEquals("Дрель", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void update_shouldUpdateOnlyName() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item existing = createItem(1L, "СтароеИмя", "СтароеОписание", true, owner);

        ItemDto dto = new ItemDto(null, "НовоеИмя", null, null, null, null, null, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.update(1L, 1L, dto);

        assertEquals("НовоеИмя", result.getName());
        assertEquals("СтароеОписание", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void create_withNonExistentRequestId_shouldThrowNotFoundException() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        ItemDto dto = new ItemDto(null, "Дрель", "Мощная", true, 99L, null, null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(1L, dto));
    }

    @Test
    void search_withBlankText_shouldReturnEmptyList() {
        List<ItemShortDto> result = itemService.search("   ", 1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void update_shouldUpdateOnlyAvailable() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item existing = createItem(1L, "Дрель", "Мощная", true, owner);
        ItemDto dto = new ItemDto(null, null, null, false, null, null, null, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

        ItemDto result = itemService.update(1L, 1L, dto);

        assertEquals("Дрель", result.getName());
        assertEquals("Мощная", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void create_invalidItemDescription_shouldThrowValidationException() {
        ItemDto dto = new ItemDto(null, "Дрель", "   ", true, null, null, null, null);

        assertThrows(ValidationException.class, () -> itemService.create(1L, dto));
    }
}