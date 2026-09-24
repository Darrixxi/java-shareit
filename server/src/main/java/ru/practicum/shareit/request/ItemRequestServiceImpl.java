package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, NewItemRequestDto dto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        return mapToDto(saved, List.of());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(req -> mapToDto(req, itemRepository.findByRequestId(req.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(req -> mapToDto(req, itemRepository.findByRequestId(req.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден"));

        List<Item> items = itemRepository.findByRequestId(requestId);
        return mapToDto(request, items);
    }

    private ItemRequestDto mapToDto(ItemRequest request, List<Item> items) {
        List<ItemRequestDto.ItemShortDto> itemDtos = items.stream()
                .map(item -> new ItemRequestDto.ItemShortDto(item.getId(), item.getName(), item.getOwner().getId()))
                .collect(Collectors.toList());

        return new ItemRequestDto(request.getId(), request.getDescription(), request.getCreated(), itemDtos);
    }
}