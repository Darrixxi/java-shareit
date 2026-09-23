package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.*;

import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {
    private final GlobalExceptionHandler errorHandler = new GlobalExceptionHandler();

    @Test
    void handleValidation() {
        Map<String, String> response = errorHandler.handleValidation(new ValidationException("Ошибка валидации"));
        assertEquals("Ошибка валидации", response.get("error"));
    }

    @Test
    void handleNotFound() {
        Map<String, String> response = errorHandler.handleNotFound(new NotFoundException("Не найдено"));
        assertEquals("Не найдено", response.get("error"));
    }

    @Test
    void handleEmailAlreadyExists() {
        Map<String, String> response = errorHandler.handleEmailAlreadyExists(new EmailAlreadyExistsException("Email уже занят"));
        assertEquals("Email уже занят", response.get("error"));
    }

    @Test
    void handleForbidden() {
        Map<String, String> response = errorHandler.handleForbidden(new ForbiddenException("Доступ запрещен"));
        assertEquals("Доступ запрещен", response.get("error"));
    }
}