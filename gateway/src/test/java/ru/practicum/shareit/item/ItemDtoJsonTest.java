package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.TextDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    private Validator validator;

    private ItemDto itemDto;
    private CreateItemDto createItemDto;
    private TextDto textDto;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        itemDto = new ItemDto();
        createItemDto = new CreateItemDto();
        textDto = new TextDto();
    }

    @Test
    public void violateNotBlankItemDto() {
        itemDto.setName("   ");
        itemDto.setDescription("    ");

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        assertThat(violations).hasSize(2);
    }

    @Test
    public void violateNotBlankCreateItemDto() {
        createItemDto.setName("   ");
        createItemDto.setDescription("    ");
        createItemDto.setAvailable(true);

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(createItemDto);

        assertThat(violations).hasSize(2);
    }

    @Test
    public void violateNotBlankTextDto() {
        textDto.setText("   ");

        Set<ConstraintViolation<TextDto>> violations = validator.validate(textDto);

        assertThat(violations).hasSize(1);
    }

    @Test
    public void violateNotNullCreateItemDto() {
        createItemDto.setName("abc");
        createItemDto.setDescription("abc");

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(createItemDto);

        assertThat(violations).hasSize(1);
    }
}
