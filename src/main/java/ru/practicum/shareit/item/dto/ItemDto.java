package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.exception.validator.NullableNotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {

    private int id;

    @NullableNotBlank
    private String name;

    @NullableNotBlank
    private String description;

    private Boolean available;
}
