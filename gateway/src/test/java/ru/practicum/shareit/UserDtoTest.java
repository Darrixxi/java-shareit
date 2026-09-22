package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createUserDto_withValidData_shouldNotHaveViolations() {
        UserDto userDto = new UserDto(1L, "Ivan", "ivan@example.com");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void createUserDto_withInvalidEmail_shouldHaveViolations() {
        UserDto userDto = new UserDto(1L, "Ivan", "not-an-email");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void createUserDto_withTooLongName_shouldHaveViolations() {
        String longName = "a".repeat(256);
        UserDto userDto = new UserDto(1L, longName, "ivan@example.com");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertFalse(violations.isEmpty());
    }
}