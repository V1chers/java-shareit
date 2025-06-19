package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequestId());

        return itemDto;
    }

    public static List<ItemDto> toDto(List<Item> items) {
        return items.stream().map(ItemMapper::toDto).toList();
    }

    public static Item fromDto(ItemDto itemDto, int userId) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setUserId(userId);

        return item;
    }

    public static Item fromCreateDto(CreateItemDto itemDto, int userId) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setUserId(userId);
        item.setRequestId(itemDto.getRequestId());

        return item;
    }

    public static ItemCommentsDto toItemCommentsDto(Item item) {
        ItemCommentsDto itemCommentsDto = new ItemCommentsDto();
        itemCommentsDto.setId(item.getId());
        itemCommentsDto.setName(item.getName());
        itemCommentsDto.setDescription(item.getDescription());
        itemCommentsDto.setAvailable(item.getAvailable());

        return itemCommentsDto;
    }

    public static ItemCommentsDto toItemCommentsDto(Item item, List<Comment> comments) {
        ItemCommentsDto itemCommentsDto = ItemMapper.toItemCommentsDto(item);
        itemCommentsDto.setComments(CommentMapper.toDto(comments));

        return itemCommentsDto;
    }

    public static AnswerDto toAnswerDto(Item item) {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setId(item.getId());
        answerDto.setName(item.getName());
        answerDto.setUserId(item.getUserId());

        return answerDto;
    }

    public static List<AnswerDto> toAnswerDto(List<Item> items) {
        return items.stream().map(ItemMapper::toAnswerDto).toList();
    }
}
