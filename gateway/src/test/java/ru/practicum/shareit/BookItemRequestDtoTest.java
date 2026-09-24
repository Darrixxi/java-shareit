package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.of(2026, 10, 1, 10, 0), LocalDateTime.of(2026, 10, 2, 10, 0));

        JsonContent<BookItemRequestDto> result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("@.itemId");
        assertThat(result).extractingJsonPathStringValue("@.start").isEqualTo("2026-10-01T10:00:00");
    }
}