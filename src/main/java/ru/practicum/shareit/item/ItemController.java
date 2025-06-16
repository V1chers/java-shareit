package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody CreateItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto,
                              @PathVariable int itemId,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemCommentsDto getItem(@PathVariable int itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemCommentsDto> getUserItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("{itemId}/comment")
    CommentDto addComment(@RequestBody @Valid TextDto text,
                          @RequestHeader("X-Sharer-User-Id") int userId,
                          @PathVariable int itemId) {
        return itemService.addComment(text, userId, itemId);
    }
}
