package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.AnswerDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {

    List<AnswerDto> items;
    private int id;
    private LocalDateTime created;
    private String description;
}
