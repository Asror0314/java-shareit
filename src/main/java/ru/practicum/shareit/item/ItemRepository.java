package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> findItemById(long itemId);

    List<Item> findAllItems(long userId);

    Optional<Item> addNewItem(Item item);

    Optional<Item> updateItem(Item newItem);

    List<Item> searchItemByText(String text);

}
