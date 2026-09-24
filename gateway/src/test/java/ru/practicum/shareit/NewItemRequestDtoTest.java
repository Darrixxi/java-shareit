package ru.practicum.shareit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewItemRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void create_withValidDescription_shouldNotHaveViolations() {
        NewItemRequestDto dto = new NewItemRequestDto("Нужна дрель для ремонта");
        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_withEmptyDescription_shouldHaveViolations() {
        NewItemRequestDto dto = new NewItemRequestDto("");
        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_withNullDescription_shouldHaveViolations() {
        NewItemRequestDto dto = new NewItemRequestDto(null);
        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_withTooLongDescription_shouldHaveViolations() {
        String longDescription = "a".repeat(513);
        NewItemRequestDto dto = new NewItemRequestDto(longDescription);
        Set<ConstraintViolation<NewItemRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
