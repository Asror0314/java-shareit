package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id = %d not found", itemId)));
        final ItemDto itemDto = ItemMapper.item2ItemDto(item);

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner().getId() == userId )
                .map(ItemMapper::item2ItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto addNewItem(ItemDto itemDto, long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final Item item = ItemMapper.itemDto2Item(itemDto, user);
        final Item newItem = itemRepository.save(item);

        return ItemMapper.item2ItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto newItemDto, long itemId, long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id = %d not found", itemId)));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("User id = '%d' not match", itemId));
        }

        newItemDto.setId(itemId);
        if (newItemDto.getStringAvailable() == null) {
            newItemDto.setAvailable(String.valueOf(item.isAvailable()));
        }
        if (newItemDto.getName() == null) {
            newItemDto.setName(item.getName());
        }
        if (newItemDto.getDescription() == null) {
            newItemDto.setDescription(item.getDescription());
        }

        final Item newItem = ItemMapper.itemDto2Item(newItemDto, user);
        newItem.setId(itemId);

        final Item updatedItem = itemRepository.save(newItem);
        return ItemMapper.item2ItemDto(updatedItem);
    }

    @Override
    @Transactional
    public List<ItemDto> searchItemByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        final List<Item> itemList = itemRepository.searchItemByText(text.toLowerCase());

        return ItemMapper.item2ItemDtoList(itemList);
    }
}
