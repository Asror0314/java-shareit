package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.DateTimeException;
import ru.practicum.shareit.exception.MismatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
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
class BookingServiceImplTest {

    private BookingService service;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private BookingRepository mockBookingRepository;
    private BookingRequestDto bookingDto;
    private Booking booking;
    private Item item;
    private String from = "0";
    private String size = "1";
    private String state = "ALL";
    private final User user = new User(1L, "name", "email");
    private final User user2 = new User(2L, "name2", "email2");
    private final Long wrongUserId = 99L;
    private final Long wrongItemId = 99L;

    @BeforeEach
    void beforeEach() {
         service = new BookingServiceImpl(mockBookingRepository, mockUserRepository, mockItemRepository);
         item = new Item(1L, "itemName", "description", true, user2, null);
         bookingDto = new BookingRequestDto(1L, LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1), Status.WAITING);
         booking = new Booking(1L, bookingDto.getStart(), bookingDto.getEnd(),
                 bookingDto.getStatus(), user, item);

    }

    @Test
    void addNewBooking() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockBookingRepository.save(any()))
                .thenReturn(booking);

        final BookingDto responseBooking = service.addNewBooking(bookingDto, user.getId());

        assertEquals(booking.getId(), responseBooking.getId());
        assertEquals(booking.getStart(), responseBooking.getStart());
        assertEquals(booking.getEnd(), responseBooking.getEnd());
        assertEquals(booking.getBooker(), responseBooking.getBooker());
    }

    @Test
    void addNewBookingWithWrongUserId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.addNewBooking(bookingDto, wrongUserId));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void addNewBookingWithItemOwnerThisUser() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        final MismatchException exception = assertThrows(MismatchException.class,
                () -> service.addNewBooking(bookingDto, user2.getId()));

        assertEquals(String.format("Item owner this user"), exception.getMessage());
    }

    @Test
    void addNewBookingWithWrongItemId() {
        bookingDto.setItemId(99L);

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Item id = %d not found", wrongItemId)));

        assertThrows(NotFoundException.class, () -> service.addNewBooking(bookingDto, user.getId()));
    }

    @Test
    void addNewBookingWithUnavailable() {
        item.setAvailable(false);

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> service.addNewBooking(bookingDto, user.getId()));

        assertEquals(String.format("Booking create from userId = %d to itemId = %d unavailable",
                user.getId(), item.getId()), exception.getMessage());
    }

    @Test
    void addNewBookingWithEndInPast() {
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));

        final DateTimeException exception = assertThrows(DateTimeException.class,
                () -> service.addNewBooking(bookingDto, user.getId()));

        assertEquals("DateTime booking entered incorrectly!", exception.getMessage());
    }

    @Test
    void addNewBookingWithEndBeforeStart() {
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        final DateTimeException exception = assertThrows(DateTimeException.class,
                () -> service.addNewBooking(bookingDto, user.getId()));

        assertEquals("DateTime booking entered incorrectly!", exception.getMessage());
    }

    @Test
    void addNewBookingWithStartEqualEnd() {
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));

        final DateTimeException exception = assertThrows(DateTimeException.class,
                () -> service.addNewBooking(bookingDto, user.getId()));

        assertEquals("DateTime booking entered incorrectly!", exception.getMessage());
    }

    @Test
    void addNewBookingWithStartInPast() {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));

        final DateTimeException exception = assertThrows(DateTimeException.class,
                () -> service.addNewBooking(bookingDto, user.getId()));

        assertEquals("DateTime booking entered incorrectly!", exception.getMessage());
    }

    @Test
    void updateBookingStatusSetApproved() {
        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(mockBookingRepository.save(any()))
                .thenReturn(booking);

        final BookingDto responseBooking = service.updateBookingStatus(booking.getId(), true, user2.getId());

        assertEquals(booking.getId(), responseBooking.getId());
        assertEquals(Status.APPROVED, responseBooking.getStatus());
    }

    @Test
    void updateBookingStatusSetRejected() {
        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(mockBookingRepository.save(any()))
                .thenReturn(booking);

        final BookingDto responseBooking = service.updateBookingStatus(booking.getId(), false, user2.getId());

        assertEquals(booking.getId(), responseBooking.getId());
        assertEquals(Status.REJECTED, responseBooking.getStatus());
    }

    @Test
    void updateBookingStatusAfterChangedStatus() {
        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito.when(mockBookingRepository.save(any()))
                .thenReturn(booking);

        final BookingDto responseBooking = service.updateBookingStatus(booking.getId(), false, user2.getId());

        assertEquals(booking.getId(), responseBooking.getId());
        assertEquals(Status.REJECTED, responseBooking.getStatus());

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> service.updateBookingStatus(booking.getId(), false, user2.getId()));

        assertEquals("Booking status has already been changed", exception.getMessage());
    }

    @Test
    void updateBookingStatusWithWrongBookingId() {
        final Long wrongBookingId = 99L;

        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Booking id = %d not found", wrongBookingId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.updateBookingStatus(wrongBookingId, true, user2.getId()));

        assertEquals(String.format("Booking id = %d not found", wrongBookingId), exception.getMessage());
    }

    @Test
    void updateBookingStatusWithWrongOwnerId() {
        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.updateBookingStatus(booking.getId(), true, wrongUserId));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void updateBookingStatusWithItemOwnerOtherUser() {
        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        final MismatchException exception = assertThrows(MismatchException.class,
                () -> service.updateBookingStatus(booking.getId(), true, user.getId()));

        assertEquals(String.format("Item owner other user"), exception.getMessage());
    }

    @Test
    void getBookingById() {
        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        final BookingDto responseBooking = service.getBookingById(booking.getId(), user.getId());

        assertEquals(booking.getId(), responseBooking.getId());
        assertEquals(booking.getBooker(), responseBooking.getBooker());
    }

    @Test
    void getBookingByIdWithWrongBookingId() {
        final Long wrongBookingId = 99L;

        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Booking id = %d not found", wrongBookingId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getBookingById(wrongBookingId, user.getId()));

        assertEquals(String.format("Booking id = %d not found", wrongBookingId), exception.getMessage());
    }

    @Test
    void getBookingByIdWithWrongUserId() {
        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getBookingById(booking.getId(), wrongUserId));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void getBookingByIdWithMismatchUserId() {
        Mockito.when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        final MismatchException exception = assertThrows(MismatchException.class,
                () -> service.getBookingById(booking.getId(), wrongUserId));

        assertEquals(String.format("user id = %d mismatch", wrongUserId), exception.getMessage());
    }

    @Test
    void getAllBookingWithPagination() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByBooker(anyLong(), anyString(), anyString()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBooking(user.getId(), null, from, size);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingWithStateAll() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByBooker_IdOrderByStartDesc(anyLong()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBooking(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingWithStateWaiting() {
        state = "WAITING";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(anyLong(), any()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBooking(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingWithStateCurrent() {
        state = "CURRENT";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository
                        .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBooking(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingWithStateFuture() {
        state = "FUTURE";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(anyLong(), any()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBooking(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingWithStatePast() {
        state = "PAST";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(anyLong(), any()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBooking(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingWithStateUnknown() {
        state = "unknown";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        final RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.getAllBooking(user.getId(), state, null, null));

        assertEquals(String.format("Unknown state: %s", state), exception.getMessage());
    }

    @Test
    void getAllBookingWithWrongUserId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final RuntimeException exception = assertThrows(NotFoundException.class,
                () -> service.getAllBooking(wrongUserId, state, null, null));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

    @Test
    void getAllBookingForOwnerWithPagination() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByItemOwner(anyLong(), anyString(), anyString()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBookingForOwner(user.getId(), null, from, size);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingForOwnerWithStateAll() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByItem_Owner_IdOrderByStartDesc(anyLong()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBookingForOwner(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingForOwnerWithStateWaiting() {
        state = "WAITING";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBookingForOwner(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingForOwnerWithStateCurrent() {
        state = "CURRENT";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository
                        .findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBookingForOwner(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingForOwnerWithStateFuture() {
        state = "FUTURE";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(anyLong(), any()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBookingForOwner(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingForOwnerWithStatePast() {
        state = "PAST";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(anyLong(), any()))
                .thenReturn(Arrays.asList(booking));

        List<BookingDto> responseBookings = service.getAllBookingForOwner(user.getId(), state, null, null);

        assertEquals(1, responseBookings.size());
        assertEquals(booking.getId(), responseBookings.get(0).getId());
    }

    @Test
    void getAllBookingForOwnerWithStateUnknown() {
        state = "unknown";

        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        final RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.getAllBookingForOwner(user.getId(), state, null, null));

        assertEquals(String.format("Unknown state: %s", state), exception.getMessage());
    }

    @Test
    void getAllBookingForOwnerWithWrongUserId() {
        Mockito.when(mockUserRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("User id = %d not found", wrongUserId)));

        final RuntimeException exception = assertThrows(NotFoundException.class,
                () -> service.getAllBookingForOwner(wrongUserId, state, null, null));

        assertEquals(String.format("User id = %d not found", wrongUserId), exception.getMessage());
    }

}