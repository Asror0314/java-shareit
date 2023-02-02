package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class ItemMapper {

    public static ItemDto item2ItemDto(final Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), String.valueOf(item.isAvailable()));
    }

    public static Item itemDto2Item(final ItemDto itemDto, final User user) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), user);
    }
}
