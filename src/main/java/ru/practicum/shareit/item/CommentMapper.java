package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) return null;
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}