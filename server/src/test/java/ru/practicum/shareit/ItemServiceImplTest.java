package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    void getAllByOwner_shouldReturnItems() {
        User owner = createUser(1L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", "Мощная", true, owner);

        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdIn(anyList())).thenReturn(List.of());

        List<ItemDto> result = itemService.findAllByOwner(1L);
        assertFalse(result.isEmpty());
        assertEquals("Дрель", result.get(0).getName());
    }

    @Test
    void addComment_whenBookingApproved_shouldSaveComment() {
        User author = createUser(1L, "Author", "a@t.ru");
        User owner = createUser(2L, "Owner", "o@t.ru");
        Item item = createItem(1L, "Дрель", "Мощная", true, owner);

        lenient().when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                any(Long.class),
                any(Long.class),
                any(BookingStatus.class),
                any(LocalDateTime.class)
        )).thenReturn(true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        assertDoesNotThrow(() -> itemService.addComment(1L, 1L, "Отличный товар!"));
    }

    @Test
    void getById_notFound_shouldThrowException() {
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.findById(1L, 99L));
    }
}