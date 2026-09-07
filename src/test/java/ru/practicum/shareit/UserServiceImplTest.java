package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_withValidData_shouldReturnUser() {
        UserDto userDto = new UserDto(null, "Иван", "ivan@mail.ru");
        User user = new User(1L, "Иван", "ivan@mail.ru");

        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_withDuplicateEmail_shouldThrowException() {
        UserDto userDto = new UserDto(null, "Иван", "ivan@mail.ru");

        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.create(userDto));
    }

    @Test
    void createUser_withEmptyName_shouldThrowException() {
        UserDto userDto = new UserDto(null, "", "ivan@mail.ru");

        assertThrows(ValidationException.class, () -> userService.create(userDto));
    }

    @Test
    void getUserById_notFound_shouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(999L));
    }
}