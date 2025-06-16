package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private Boolean available;

    @Column
    private int userId;
}
