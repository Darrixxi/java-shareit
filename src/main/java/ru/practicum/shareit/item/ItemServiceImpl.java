package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        validateItem(itemDto);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с id=" + ownerId + " не найден"
                ));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        "Вещь с id=" + itemId + " не найдена"
                ));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException(
                    "Вещь с id=" + itemId + " не найдена у пользователя с id=" + ownerId
            );
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
    public ItemDto findById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        "Вещь с id=" + itemId + " не найдена"
                ));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAllByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemShortDto> search(String text, Long userId) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.search(text, userId).stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList());
    }

    private void validateItem(ItemDto dto) {
        if (dto == null) {
            throw new ValidationException("Тело запроса не может быть пустым");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        if (dto.getAvailable() == null) {
            throw new ValidationException("Статус доступности должен быть указан");
        }
    }
}
