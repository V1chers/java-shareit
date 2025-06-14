package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto createItem(CreateItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    ItemCommentsDto getItem(int itemId);

    List<ItemCommentsDto> getUserItems(int userId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(TextDto text, int authorId, int itemId);
}
