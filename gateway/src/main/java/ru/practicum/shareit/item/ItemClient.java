package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.TextDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(CreateItemDto itemDto, long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, int itemId, long userId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return patch("/{itemId}", userId, parameters, itemDto);
    }

    public ResponseEntity<Object> getItem(int itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return get("/{itemId}", null, parameters);
    }

    public ResponseEntity<Object> getUserItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> addComment(TextDto text, long userId, int itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        return post("/{itemId}/comment", userId, parameters, text);
    }
}
