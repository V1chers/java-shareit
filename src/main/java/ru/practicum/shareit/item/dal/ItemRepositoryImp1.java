package ru.practicum.shareit.item.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemRepositoryImp1 {
    private final HashMap<Integer, Item> items;

    public Item createItem(Item item) {
        int id = createId();

        item.setId(id);
        items.put(id, item);

        return item;
    }

    public Item updateItem(Item item) {
        Item updatingItem = items.get(item.getId());

        if (item.getName() != null) {
            updatingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatingItem.setAvailable(item.getAvailable());
        }

        return updatingItem;
    }

    public Optional<Item> getItem(int itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public List<Item> getUserItems(int userId) {
        return items.values().stream()
                .filter(item -> item.getUserId() == userId)
                .toList();
    }

    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .toList();
    }

    private Integer createId() {
        Optional<Integer> maxItemId = items.keySet().stream()
                .max(Integer::compare);

        return maxItemId.orElse(0) + 1;
    }
}
