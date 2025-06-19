package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {
    private Validator validator;

    private UserDto userDto;
    private CreateUserDto createUserDto;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        createUserDto = new CreateUserDto();
        userDto = new UserDto();
    }

    @Test
    public void violateEmailUserDto() {
        userDto.setEmail("123");
        userDto.setName("123");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).hasSize(1);
    }

    @Test
    public void violateEmailCreateUserDto() {
        createUserDto.setEmail("123");
        createUserDto.setName("123");

        Set<ConstraintViolation<CreateUserDto>> violations2 = validator.validate(createUserDto);

        assertThat(violations2).hasSize(1);
    }

    @Test
    public void violateNotBlankUserDto() {
        userDto.setEmail("   ");
        userDto.setName("   ");

        Set<ConstraintViolation<UserDto>> userDtoViolations = validator.validate(userDto);

        assertThat(userDtoViolations).hasSize(3);
    }

    @Test
    public void violateNotBlankCreateUserDto() {
        createUserDto.setEmail("   ");
        createUserDto.setName("   ");

        Set<ConstraintViolation<CreateUserDto>> createUserDtoViolations = validator.validate(createUserDto);

        assertThat(createUserDtoViolations).hasSize(3);
    }
}
