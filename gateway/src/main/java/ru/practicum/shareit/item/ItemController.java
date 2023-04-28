package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return  itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemClient.getAllItems(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addNewItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemClient.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestBody ItemDto itemDto,
            @PathVariable long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByText(
            @RequestParam String text,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return itemClient.searchItemByText(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long itemId
    ) {
        return itemClient.addNewComment(commentDto, userId, itemId);
    }
}
