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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void update_emailAlreadyExists_shouldThrowException() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Old");
        existingUser.setEmail("old@test.com");

        UserDto dto = new UserDto(null, "New", "other@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("other@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(1L, dto));
    }

    @Test
    void update_shouldUpdateUserSuccessfully() {
        User existing = new User();
        existing.setId(1L);
        existing.setName("OldName");
        existing.setEmail("old@test.com");

        UserDto dto = new UserDto(null, "NewName", "new@test.com");
        User updated = new User();
        updated.setId(1L);
        updated.setName("NewName");
        updated.setEmail("new@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserDto result = userService.update(1L, dto);
        assertEquals("NewName", result.getName());
        assertEquals("new@test.com", result.getEmail());
    }

    @Test
    void updateUser_withExistingEmail_shouldThrowEmailAlreadyExistsException() {
        User existing = new User();
        existing.setId(1L);
        existing.setName("Old");
        existing.setEmail("old@t.ru");

        UserDto updateDto = new UserDto(null, "New Name", "another@t.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("another@t.ru")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void updateUser_onlyName() {
        User existing = new User();
        existing.setId(1L);
        existing.setName("Old");
        existing.setEmail("old@t.ru");

        UserDto updateDto = new UserDto(null, "New Name", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDto result = userService.update(1L, updateDto);
        assertEquals("New Name", result.getName());
        assertEquals("old@t.ru", result.getEmail());
    }

    @Test
    void findAll_shouldReturnListOfUsers() {
        User user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@t.ru");
        when(userRepository.findAll()).thenReturn(List.of(user));

        var result = userService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    void deleteUser_shouldCallRepository() {
        userService.delete(1L);
        org.mockito.Mockito.verify(userRepository).deleteById(1L);
    }
}