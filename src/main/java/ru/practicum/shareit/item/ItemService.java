package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

public interface ItemService {

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getAllItems(long userId);

    ItemDto addNewItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto newItemDto, long itemId, long userId);

    List<ItemDto> searchItemByText(String text);

    CommentDto addNewComment(CommentDto commentDto, long userId, long itemId);

}
