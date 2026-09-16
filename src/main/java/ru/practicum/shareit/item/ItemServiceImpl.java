package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        validateItem(itemDto);
        User owner = getUserById(ownerId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        Item item = getItemById(itemId);

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не принадлежит пользователю с id=" + ownerId);
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemShortDto> search(String text, Long userId) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, String text) {
        Item item = getItemById(itemId);
        User author = getUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        if (!hasPastApprovedBooking(userId, itemId, now)) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду или аренда ещё не завершена");
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return mapToItemDtoWithBookings(item, comments);
    }

    @Override
    public List<ItemDto> findAllByOwner(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Comment> allComments = commentRepository.findAllByItemIdIn(itemIds);

        Map<Long, List<Comment>> commentsByItem = allComments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
                .map(item -> mapToItemDtoWithBookings(item, commentsByItem.getOrDefault(item.getId(), List.of())))
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
    }

    private boolean hasPastApprovedBooking(Long userId, Long itemId, LocalDateTime now) {
        return bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, now);
    }

    private void validateItem(ItemDto dto) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank() ||
                dto.getDescription() == null || dto.getDescription().isBlank() ||
                dto.getAvailable() == null) {
            throw new ValidationException("Некорректные данные вещи");
        }
    }

    private ItemDto mapToItemDtoWithBookings(Item item, List<Comment> comments) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime lastBooking = bookingRepository
                .findCurrentByItemId(item.getId(), now)
                .map(Booking::getStart)
                .orElse(null);

        LocalDateTime nextBooking = bookingRepository
                .findTop1ByItemIdAndStartGreaterThanEqualAndStatusOrderByStartAsc(
                        item.getId(), now, BookingStatus.APPROVED)
                .map(Booking::getStart)
                .orElse(null);

        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                commentDtos
        );
    }
}