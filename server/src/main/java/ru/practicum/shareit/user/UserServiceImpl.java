package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        validateUser(userDto);
        checkEmailExists(userDto.getEmail());

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User existing = getUserById(userId);

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!existing.getEmail().equalsIgnoreCase(userDto.getEmail())) {
                checkEmailExists(userDto.getEmail());
            }
            existing.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existing.setName(userDto.getName());
        }

        User updatedUser = userRepository.save(existing);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto findById(Long userId) {
        User user = getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Пользователь с email " + email + " уже существует");
        }
    }

    private void validateUser(UserDto dto) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank() ||
                dto.getEmail() == null || dto.getEmail().isBlank() || !dto.getEmail().contains("@")) {
            throw new ValidationException("Некорректные данные пользователя");
        }
    }
}