package ru.practicum.shareit.item.dto;


import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {

    private int id;

    private String text;

    private int itemId;

    private String authorName;

    private Instant created;
}
