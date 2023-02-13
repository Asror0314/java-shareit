package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return  itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemService.getAllItems(userId);
    }

    @PostMapping
    public ItemDto addNewItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable long itemId, @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(
            @RequestParam String text,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return itemService.searchItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @PathVariable long itemId
    ) {
        return itemService.addNewComment(commentDto, userId, itemId);
    }
}
