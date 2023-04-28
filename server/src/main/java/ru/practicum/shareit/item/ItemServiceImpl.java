package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.Status;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto addNewItem(ItemDto itemDto, long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final ItemRequest request = requestRepository.findById(itemDto.getRequestId()).orElse(null);
        final Item item = ItemMapper.map2Item(itemDto, user, request);
        final Item newItem = itemRepository.save(item);

        return ItemMapper.map2ItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto newItemDto, long itemId, long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id = %d not found", itemId)));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("User id = '%d' not match", userId));
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

        final Item newItem = ItemMapper.map2Item(newItemDto, user, item.getRequest());
        newItem.setId(itemId);

        final Item updatedItem = itemRepository.save(newItem);
        return ItemMapper.map2ItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id = %d not found", itemId)));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));

        final BookingItemDto lastBooking = bookingRepository
                .findLastBookingByItem_Id(userId, itemId, LocalDateTime.now())
                .stream()
                .findFirst()
                .map(BookingMapper::map2BookingItemDto)
                .orElse(null);
        final BookingItemDto nextBooking = bookingRepository
                .findNextBookingByItem_Id(userId, itemId, LocalDateTime.now())
                .stream()
                .findFirst()
                .map(BookingMapper::map2BookingItemDto)
                .orElse(null);

        List<CommentDto> comments = commentRepository.findCommentByItem_Id(itemId)
                .stream()
                .map(CommentMapper::map2CommentDto)
                .collect(Collectors.toList());

        final ItemDto itemDto = ItemMapper.map2ItemDto(item);

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments);

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(item -> getItemById(item.getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemDto> searchItemByText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        final List<Item> itemList = itemRepository.searchItemByText(text.toLowerCase());

        return ItemMapper.map2ItemDtoList(itemList);
    }

    @Override
    public CommentDto addNewComment(
            CommentDto commentDto,
            long userId,
            long itemId
    ) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item id = %d not found", itemId)));

        bookingRepository
                .findBookingByBookerIdAndItemId(userId, itemId, Status.APPROVED.name())
                .orElseThrow(() -> new ValidationException(
                        String.format("User id = %d the user has not booked this item id = %d", userId, itemId)));

        final Comment comment = CommentMapper.map2Comment(commentDto, user, item);

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.map2CommentDto(savedComment);
    }
}
