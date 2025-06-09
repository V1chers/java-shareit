package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(CreateItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int itemId, int userId);

    ItemDto getItem(int itemId);

    List<ItemDto> getUserItems(int userId);

    List<ItemDto> searchItems(String text);
}
