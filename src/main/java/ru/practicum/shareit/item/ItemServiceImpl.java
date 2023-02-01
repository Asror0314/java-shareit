package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
        final Item item = itemRepository.findItemById(itemId).get();
        final ItemDto itemDto = ItemMapper.item2ItemDto(item);

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        return itemRepository.findAllItems(userId)
                .stream()
                .map(item -> ItemMapper.item2ItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addNewItem(ItemDto itemDto, long userId) {
        final User user = userRepository.findUserById(userId).get();
        final Item item = ItemMapper.itemDto2Item(itemDto, user);
        final Item newItem = itemRepository.addNewItem(item).get();
        return ItemMapper.item2ItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(ItemDto newItemDto, long itemId, long userId) {
        final User user = userRepository.findUserById(userId).get();
        final Item item = itemRepository.findItemById(itemId).get();

        if(!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("User id = '%d' not match", itemId));
        }

        newItemDto.setId(itemId);
        if(newItemDto.getStringAvailable() == null) {
            newItemDto.setAvailable(String.valueOf(item.isAvailable()));
        }
        if(newItemDto.getName() == null) {
            newItemDto.setName(item.getName());
        }
        if(newItemDto.getDescription() == null) {
            newItemDto.setDescription(item.getDescription());
        }

        final Item newItem = ItemMapper.itemDto2Item(newItemDto, user);
        final Item updatedItem = itemRepository.updateItem(newItem).get();
        return ItemMapper.item2ItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        if(text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchItemByText(text)
                .stream()
                .map(item -> ItemMapper.item2ItemDto(item))
                .collect(Collectors.toList());
    }
}
