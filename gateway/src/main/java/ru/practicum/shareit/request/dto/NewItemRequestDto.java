package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewItemRequestDto {
    @NotBlank(message = "Описание запроса не может быть пустым")
    @Size(max = 512, message = "Описание не может превышать 512 символов")
    private String description;
}