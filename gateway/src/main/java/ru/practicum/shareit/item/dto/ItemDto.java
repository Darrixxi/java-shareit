package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 255, message = "Название не может превышать 255 символов")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 512, message = "Описание не может превышать 512 символов")
    private String description;

    @NotNull(message = "Поле available обязательно")
    private Boolean available;

    private Long requestId;

    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments;
}