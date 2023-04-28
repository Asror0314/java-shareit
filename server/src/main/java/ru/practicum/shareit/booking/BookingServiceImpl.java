package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.DateTimeException;
import ru.practicum.shareit.exception.MismatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addNewBook(BookingRequestDto bookingDto, Long userId) {
        bookingDto.setStatus(Status.WAITING);

        if (bookingDto.getEnd().isBefore(LocalDateTime.now())
                || bookingDto.getStart().isBefore(LocalDateTime.now())
                || !bookingDto.getStart().isBefore(bookingDto.getEnd())
        ) {
            throw new DateTimeException("DateTime booking entered incorrectly!");
        }

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Item id = %d not found", bookingDto.getItemId())));

        if (userId.equals(item.getOwner().getId())) {
            throw new MismatchException("Item owner this user");
        }

        if (!item.isAvailable()) {
            throw new ValidationException(String.format("Booking create from userId = %d to itemId = %d unavailable",
                    userId, item.getId()));
        }

        final Booking booking = BookingMapper.map2Booking(bookingDto, user, item);
        final Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.map2BookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookStatus(long bookingId, boolean status, Long ownerId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id = %d not found", bookingId)));
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", ownerId)));

        if (!ownerId.equals(booking.getItem().getOwner().getId())) {
            throw new MismatchException("Item owner other user");
        }

        if (!Status.WAITING.equals(booking.getStatus())) {
            throw new ValidationException("Booking status has already been changed");
        }

        if (status) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        final Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.map2BookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(long bookingId, Long userId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id = %d not found", bookingId)));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));

        final Long bookerId = booking.getBooker().getId();
        final Long ownerId = booking.getItem().getOwner().getId();

        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new MismatchException(String.format("user id = %d mismatch", userId));
        }

        return BookingMapper.map2BookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings(long bookerId, String state, int from, int size) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", bookerId)));

        switch (state) {
            case "ALL":
                return bookingRepository
                    .findAllByBookerId(bookerId, from, size)
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
            case "WAITING":
            case "REJECTED": {
                return bookingRepository
                        .findAllByBookerIdWithStatus(bookerId, state, from, size)
                        .stream()
                        .map(BookingMapper::map2BookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT":
                return bookingRepository
                    .findAllByBookerIdWithStatusCurrent(bookerId,from, size)
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository
                    .findAllByBookerIdWithStatusFuture(bookerId, from, size)
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
            case "PAST":
                return bookingRepository
                    .findAllByBookerIdWithStatusPast(bookerId, from, size)
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
            default:
                throw new RuntimeException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingDto> getAllBookingsForOwner(long ownerId, String state, int from, int size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", ownerId)));

        switch (state) {
            case "ALL":
                return bookingRepository
                        .findAllByOwnerId(ownerId, from, size)
                        .stream()
                        .map(BookingMapper::map2BookingDto)
                        .collect(Collectors.toList());
            case "WAITING":
            case "REJECTED": {
                return bookingRepository
                        .findAllByOwnerIdWithStatus(ownerId, state, from, size)
                        .stream()
                        .map(BookingMapper::map2BookingDto)
                        .collect(Collectors.toList());
            }
            case "CURRENT":
                return bookingRepository
                        .findAllByOwnerIdWithStatusCurrent(ownerId, from, size)
                        .stream()
                        .map(BookingMapper::map2BookingDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository
                        .findAllByOwnerIdWithStatusFuture(ownerId, from, size)
                        .stream()
                        .map(BookingMapper::map2BookingDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository
                        .findAllByOwnerIdWithStatusPast(ownerId, from, size)
                        .stream()
                        .map(BookingMapper::map2BookingDto)
                        .collect(Collectors.toList());
            default:
                throw new RuntimeException(String.format("Unknown state: %s", state));
        }
    }

}
