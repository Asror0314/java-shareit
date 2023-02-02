package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    ItemDto getItemById(long itemId);

    List<ItemDto> getAllItems(long userId);

    ItemDto addNewItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto newItemDto, long itemId, long userId);

    List<ItemDto> searchItemByText(String text);

}
