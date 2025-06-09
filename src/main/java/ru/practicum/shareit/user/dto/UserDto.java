package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.exception.validator.NullableNotBlank;

@Data
public class UserDto {
    private int id;

    @NullableNotBlank
    private String email;

    @NullableNotBlank
    private String name;
}
