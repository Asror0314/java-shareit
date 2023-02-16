package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto map2ItemDto(Item item) {
        final ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(String.valueOf(item.isAvailable()));

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public static List<ItemDto> map2ItemDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::map2ItemDto)
                .collect(Collectors.toList());
    }

    public static Item map2Item(ItemDto itemDto, User user, ItemRequest request) {
        final Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(request);

        return item;
    }

    public static ItemDtoForRequest map2ItemDtoForRequest(Item item) {
        final ItemDtoForRequest itemDto = new ItemDtoForRequest();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(String.valueOf(item.isAvailable()));
        itemDto.setRequestId(item.getRequest().getId());

        return itemDto;
    }
}
