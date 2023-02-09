package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl {

    private final Map<Long, Item> items = new HashMap<>();
    private long generatedId = 0;

    public Optional<Item> findItemById(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(String.format("Item id = '%d' not found", itemId));
        }

        final Item item = items.get(itemId);
        return Optional.of(item);
    }

    public List<Item> findAllItems(long userId) {
        return items.values()
                .stream()
                .filter(
                        item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<Item> addNewItem(Item item) {
        item.setId(getGeneratedId());
        items.put(item.getId(), item);

        return Optional.of(item);
    }

    public Optional<Item> updateItem(Item newItem) {
        items.put(newItem.getId(), newItem);
        return Optional.of(newItem);
    }

    public List<Item> searchItemByText(String text) {
        return items.values()
                .stream()
                .filter(
                        item -> item.getDescription().toLowerCase().matches("(.*)" + text.toLowerCase() + "(.*)")
                                && item.isAvailable() == true
                            || item.getName().toLowerCase().matches("(.*)" + text.toLowerCase() + "(.*)"))
                .collect(Collectors.toList());
    }

    private long getGeneratedId() {
        return ++generatedId;
    }
}
