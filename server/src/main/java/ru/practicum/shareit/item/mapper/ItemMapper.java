package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        if (item == null) return null;

        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                null,
                null,
                commentDtos
        );
    }

    public static ItemDto toItemDto(Item item) {
        return toItemDto(item, List.of());
    }

    public static ItemShortDto toItemShortDto(Item item) {
        if (item == null) return null;
        return new ItemShortDto(item.getId(), item.getName());
    }

    public static Item toItem(ItemDto dto) {
        if (dto == null) {
            return null;
        }
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setRequestId(dto.getRequestId());
        return item;
    }
}