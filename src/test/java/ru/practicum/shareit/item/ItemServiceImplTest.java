package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private RequestRepository mockRequestRepository;
    private ItemService service;
    private User user = new User(1L, "name", "email");
    private Long wrongUserId = 99L;
    private Long wrongItemId = 99L;
    private Item item;
    private Item item2;
    private ItemDto itemDto = new ItemDto();
    private CommentDto commentDto = new CommentDto();


    @BeforeEach
    void beforeEach() {
        service = new ItemServiceImpl(
                mockItemRepository,
                mockUserRepository,
                mockBookingRepository,
                mockCommentRepository,
                mockRequestRepository);
        user = new User(1L, "name", "email");
        item = new Item(1L, "itemName", "description", true, user, null);
        item2 = new Item(2L, "itemName2", "description2", false, user, null);
        itemDto.setName("itemName");
        itemDto.setDescription("description");
        itemDto.setAvailable("true");
    }

    @Test
    void addNewItem() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Mockito.when(mockItemRepository.save(any()))
                .thenReturn(item);

        final ItemDto newItem = service.addNewItem(itemDto, user.getId());

        assertEquals(item.getId(), newItem.getId());
        assertEquals(itemDto.getName(), newItem.getName());
        assertEquals(itemDto.getDescription(), newItem.getDescription());
        assertEquals(itemDto.getAvailable(), newItem.getAvailable());
    }

    @Test
    void addNewItemWithWrongUserId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.addNewItem(itemDto, wrongUserId));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void updateItem() {
        itemDto.setName("updateName");
        item.setName("updateName");

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockItemRepository.save(any()))
                .thenReturn(item);

        final ItemDto newItem = service.updateItem(itemDto, item.getId(), user.getId());

        assertEquals(item.getId(), newItem.getId());
        assertEquals(itemDto.getName(), newItem.getName());
        assertEquals(itemDto.getDescription(), newItem.getDescription());
        assertEquals(itemDto.getAvailable(), newItem.getAvailable());
    }

    @Test
    void updateItemWithWrongUserId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.updateItem(itemDto, item.getId(), wrongUserId));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void updateItemWithWrongItemId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Item id = %d not found", wrongItemId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.updateItem(itemDto, wrongItemId, user.getId()));

        assertEquals(String.format("Item id = %d not found", wrongItemId), exception.getMessage());
    }

    @Test
    void updateItemWithOtherUserId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.updateItem(itemDto, item.getId(), wrongUserId));

        assertEquals(String.format("User id = '%d' not match", wrongUserId), exception.getMessage());
    }

    @Test
    void getItemById() {
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findLastBookingByItem_Id(anyLong(), anyLong(), any()))
                .thenReturn(Arrays.asList());
        Mockito.when(mockBookingRepository.findNextBookingByItem_Id(anyLong(), anyLong(), any()))
                .thenReturn(Arrays.asList());
        Mockito.when(mockCommentRepository.findCommentByItem_Id(anyLong()))
                .thenReturn(Arrays.asList());

        final ItemDto responseItem = service.getItemById(item.getId(), user.getId());

        assertEquals(item.getId(), responseItem.getId());
        assertEquals(item.getName(), responseItem.getName());
        assertEquals(item.getDescription(), responseItem.getDescription());
    }

    @Test
    void getItemByIdWithLastAndNextBooking() {
        final Booking nextBooking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), Status.WAITING,
                                    user, item);
        final Booking lastBooking = new Booking(2L, LocalDateTime.now(), LocalDateTime.now(), Status.WAITING,
                user, item);

        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findLastBookingByItem_Id(anyLong(), anyLong(), any()))
                .thenReturn(Arrays.asList(lastBooking));
        Mockito.when(mockBookingRepository.findNextBookingByItem_Id(anyLong(), anyLong(), any()))
                .thenReturn(Arrays.asList(nextBooking));
        Mockito.when(mockCommentRepository.findCommentByItem_Id(anyLong()))
                .thenReturn(Arrays.asList());

        final ItemDto responseItem = service.getItemById(item.getId(), user.getId());

        assertEquals(item.getId(), responseItem.getId());
        assertEquals(item.getName(), responseItem.getName());
        assertEquals(item.getDescription(), responseItem.getDescription());
        assertEquals(lastBooking.getId(), responseItem.getLastBooking().getId());
        assertEquals(lastBooking.getBooker().getId(), responseItem.getLastBooking().getBookerId());
    }

    @Test
    void getItemByIdWithWrongItemId() {
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Item id = %d not found", wrongItemId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getItemById(wrongItemId, user.getId()));

        assertEquals(String.format("Item id = %d not found", wrongItemId), exception.getMessage());
    }

    @Test
    void getItemByIdWithWrongUserId() {
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getItemById(item.getId(), wrongUserId));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void getAllItems() {
        Mockito
                .when(mockItemRepository.findAll())
                .thenReturn(Arrays.asList(item, item2));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findLastBookingByItem_Id(anyLong(), anyLong(), any()))
                .thenReturn(Arrays.asList());
        Mockito.when(mockBookingRepository.findNextBookingByItem_Id(anyLong(), anyLong(), any()))
                .thenReturn(Arrays.asList());
        Mockito.when(mockCommentRepository.findCommentByItem_Id(anyLong()))
                .thenReturn(Arrays.asList());
        Mockito
                .when(mockItemRepository.findById(Mockito.anyLong()))
                .thenAnswer(invocationOnMock -> {
                    Long itemId = invocationOnMock.getArgument(0, Long.class);
                    if(itemId == 1) {
                        return Optional.of(item);
                    } else {
                        return Optional.of(item2);
                    }
                });

        final List<ItemDto> responseItems = service.getAllItems(user.getId());

        assertEquals(2, responseItems.size());
        assertEquals(1L, responseItems.get(0).getId());
        assertEquals(2L, responseItems.get(1).getId());
    }

    @Test
    void searchItemByText() {
        final String text = "desc";

        Mockito.when(mockItemRepository.searchItemByText(anyString()))
                .thenReturn(Arrays.asList(item, item2));

        final List<ItemDto> responseItems = service.searchItemByText(text);

        assertEquals(2, responseItems.size());
        assertEquals(1L, responseItems.get(0).getId());
        assertEquals(2L, responseItems.get(1).getId());
    }

    @Test
    void searchItemByTextWithBlankText() {
        final String text = " ";
        final List<ItemDto> responseItems = service.searchItemByText(text);

        assertEquals(0, responseItems.size());
    }

    @Test
    void addNewComment() {
        final Comment comment = new Comment(1L, "comment", LocalDateTime.now(), item, user);
        commentDto.setText("comment");

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockBookingRepository.findBookingByBookerIdAndItemId(anyLong(), anyLong(), any(), any()))
                .thenReturn(Optional.of(new Booking()));
        Mockito.when(mockCommentRepository.save(any()))
                .thenReturn(comment);

        final CommentDto responseCommentDto = service.addNewComment(commentDto, user.getId(), comment.getItem().getId());

        assertEquals(comment.getId(), responseCommentDto.getId());
        assertEquals(comment.getAuthor().getName(), responseCommentDto.getAuthorName());
        assertEquals(comment.getText(), responseCommentDto.getText());
    }

    @Test
    void addNewCommentWithWrongUserId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.addNewComment(commentDto, wrongUserId, item.getId()));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());

    }

    @Test
    void addNewCommentWithWrongItemId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Item id = %d not found", wrongItemId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.addNewComment(commentDto, user.getId(), wrongItemId));

        assertEquals(String.format("Item id = %d not found", wrongItemId), exception.getMessage());
    }

    @Test
    void addNewCommentWithWrongBookerId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item2));
        Mockito.when(mockBookingRepository.findBookingByBookerIdAndItemId(anyLong(), anyLong(), any(), any()))
                .thenThrow(new ValidationException(
                        String.format("User id = %d the user has not booked this item id = %d",
                                wrongItemId, item2.getId())));

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> service.addNewComment(commentDto, wrongUserId, item2.getId()));

        assertEquals(String.format("User id = %d the user has not booked this item id = %d",
                wrongUserId, item2.getId()), exception.getMessage());
    }

}