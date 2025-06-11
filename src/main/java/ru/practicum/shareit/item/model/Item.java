package ru.practicum.shareit.item.model;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    private int id;

    private String name;

    private String description;

    private Boolean available;

    private int userId;
}
