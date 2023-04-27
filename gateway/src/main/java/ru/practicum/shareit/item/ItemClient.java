package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";
    private static final String API_PREFIX_SEARCH = "/search";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                    .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                    .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                    .build()
        );
    }

    public ResponseEntity<Object> getItemById(long itemId, long userId) {
        return  get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> addNewItem(ItemDto itemDto, long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, long itemId, long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> searchItemByText(String text, long userId) {
        final Map<String, Object> parameters = Map.of("text", text);

        return get(API_PREFIX_SEARCH + "?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> addNewComment( CommentDto commentDto, long userId, long itemId) {
        return null;
    }
}
