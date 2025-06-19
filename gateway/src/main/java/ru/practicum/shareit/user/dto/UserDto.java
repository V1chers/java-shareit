package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import ru.practicum.shareit.exception.validator.NullableNotBlank;

@Data
public class UserDto {
    @NullableNotBlank
    @Email
    private String email;

    @NullableNotBlank
    private String name;
}
