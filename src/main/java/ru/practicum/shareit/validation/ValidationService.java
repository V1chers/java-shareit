package ru.practicum.shareit.validation;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exception.exceptions.ConditionsNotMetException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Optional;

public class ValidationService {

    public static <t, k> void isExist(JpaRepository<t, k> repository, k id, String message) {
        Optional<t> object = repository.findById(id);

        if (object.isEmpty()) {
            throw new NotFoundException(message + ": " + id);
        }
    }

    public static void isItemAvailable(ItemRepository itemRepository, int itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не была найдена: " + itemId));

        if (!item.getAvailable()) {
            throw new ConditionsNotMetException("Вещь недоступна для бронирования");
        }
    }
}
