package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.AnswerDto;
import ru.practicum.shareit.request.ItemRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setCreated(toLocalDateTime(itemRequest.getCreated()));
        itemRequestDto.setDescription(itemRequest.getDescription());

        return itemRequestDto;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<AnswerDto> answerDtos) {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(answerDtos);

        return itemRequestDto;
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
