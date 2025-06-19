package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.DescriptionDto;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DescriptionDtoJsonTest {
    private final JacksonTester<DescriptionDto> json;

    private Validator validator;

    private DescriptionDto descriptionDto;

    @BeforeEach
    public void setUp() {
        descriptionDto = new DescriptionDto();
        descriptionDto.setDescription("123");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testDescriptionDto() throws IOException {
        descriptionDto.setDescription("123");

        JsonContent<DescriptionDto> result = json.write(descriptionDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("123");
    }

    @Test
    public void validateBlankDescriptionField() {
        descriptionDto.setDescription("   ");

        Set<ConstraintViolation<DescriptionDto>> violations = validator.validate(descriptionDto);

        assertThat(violations).hasSize(1);
    }
}
