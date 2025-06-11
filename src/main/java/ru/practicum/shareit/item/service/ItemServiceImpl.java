package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dal.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto createItem(CreateItemDto itemDto, int userId) {
        log.info("начинаем создания предмета: {}, userId = {}", itemDto, userId);
        isUserExists(userId);

        Item item = ItemMapper.fromCreateDto(itemDto, userId);
        item = itemRepository.createItem(item);

        log.info("Предмет создан: {}", item);
        return ItemMapper.toDto(item);
    }

    public ItemDto updateItem(ItemDto itemDto, int itemId, int userId) {
        isUserExists(userId);
        isItemExists(itemId);

        itemDto.setId(itemId);
        Item item = ItemMapper.fromDto(itemDto, userId);
        item = itemRepository.updateItem(item);

        return ItemMapper.toDto(item);
    }

    public ItemDto getItem(int itemId) {
        Optional<Item> item = itemRepository.getItem(itemId);

        if (item.isPresent()) {
            log.warn("Данный предмет не найден: {}", itemId);
            return ItemMapper.toDto(item.get());
        } else {
            throw new NotFoundException("Пользователь с данным id не был найден");
        }
    }

    public List<ItemDto> getUserItems(int userId) {
        isUserExists(userId);

        List<Item> items = itemRepository.getUserItems(userId);

        return ItemMapper.toDto(items);
    }

    public List<ItemDto> searchItems(String text) {
        log.info("Происходит поиск вещей по запросу : {}", text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return ItemMapper.toDto(itemRepository.searchItems(text));
    }

    private void isItemExists(int itemId) {
        getItem(itemId);
    }

    private void isUserExists(int userId) {
        Optional<User> user = userRepository.getUser(userId);

        if (user.isEmpty()) {
            log.warn("Данный пользователь не найден: {}", userId);
            throw new NotFoundException("Данный пользователь не найден");
        }
    }
}
