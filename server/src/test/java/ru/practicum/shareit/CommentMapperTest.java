package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentMapperTest {
    @Test
    void toCommentDto_shouldMapFields() {
        User author = new User();
        author.setId(1L);
        author.setName("Ivan");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Отлично");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertNotNull(dto);
        assertEquals("Отлично", dto.getText());
        assertEquals("Ivan", dto.getAuthorName());
    }
}