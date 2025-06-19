package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookItemRequestDto> json;

    private Validator validator;

    private BookItemRequestDto bookItemRequestDto;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        bookItemRequestDto = new BookItemRequestDto();
    }

    @Test
    public void testLocalDatePattern() throws IOException {
        bookItemRequestDto.setStart(LocalDateTime.of(2025, 6, 15, 12, 0));
        bookItemRequestDto.setEnd(LocalDateTime.of(2025, 6, 15, 12, 0));

        JsonContent<BookItemRequestDto> result = json.write(bookItemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-06-15T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-06-15T12:00:00");
    }

    @Test
    public void violateFutureFieldBookItemRequestDto() {
        bookItemRequestDto.setStart(LocalDateTime.now().minusHours(1));
        bookItemRequestDto.setEnd(LocalDateTime.now().minusHours(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookItemRequestDto);

        assertThat(violations).hasSize(2);
    }
}
