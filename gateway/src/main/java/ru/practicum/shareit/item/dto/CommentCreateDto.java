package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 512, message = "Текст комментария не может превышать 512 символов")
    private String text;
}