package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void create_shouldSaveAndReturnRequest() {
        Long userId = 1L;
        NewItemRequestDto dto = new NewItemRequestDto("Нужна дрель");

        ItemRequest savedRequest = new ItemRequest();
        savedRequest.setId(1L);
        savedRequest.setDescription("Нужна дрель");
        savedRequest.setRequesterId(userId);
        savedRequest.setCreated(LocalDateTime.now());

        when(requestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);

        ItemRequestDto result = itemRequestService.create(userId, dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Нужна дрель", result.getDescription());

        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getUserRequests_shouldReturnListWithItems() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Ivan");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setOwner(user);

        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        List<ItemRequestDto> result = itemRequestService.getUserRequests(userId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getItems().get(0).getId());
        assertEquals("Дрель", result.get(0).getItems().get(0).getName());
    }

    @Test
    void getRequestById_whenNotFound_shouldThrowException() {
        Long userId = 1L;
        Long requestId = 999L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(userId, requestId));
    }

    @Test
    void getRequestById_whenFound_shouldReturnWithItems() {
        Long userId = 1L;
        Long requestId = 1L;

        User owner = new User();
        owner.setId(2L);
        owner.setName("Petr");

        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        request.setDescription("Нужна дрель");
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");
        item.setOwner(owner);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of(item));

        ItemRequestDto result = itemRequestService.getRequestById(userId, requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
        assertEquals(1, result.getItems().size());
        assertEquals(2L, result.getItems().get(0).getOwnerId());
    }

    @Test
    void getRequestById_notFound_shouldThrowException() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 99L));
    }

    @Test
    void getRequestById_notFound_shouldThrowNotFoundException() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 99L));
    }
}
