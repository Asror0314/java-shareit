package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto item2ItemDto(final Item item) {
        final ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(String.valueOf(item.isAvailable()));

        return itemDto;
    }

    public static List<ItemDto> item2ItemDtoList(final List<Item> items) {
        return items.stream()
                .map(ItemMapper::item2ItemDto)
                .collect(Collectors.toList());
    }

    public static Item itemDto2Item(final ItemDto itemDto, final User user) {
        final Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);

        return item;
    }
}
