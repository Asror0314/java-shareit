package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponceDto;
import ru.practicum.shareit.exception.DateTimeException;
import ru.practicum.shareit.exception.MismatchException;
import ru.practicum.shareit.exception.NotFoundException;
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
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<BookingResponceDto> getAllBooking(long bookerId, String state) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", bookerId)));
        if (state.equals("ALL")) {
            return bookingRepository
                    .findAllByBooker_IdOrderByStartDesc(bookerId)
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("WAITING") || state.equals("REJECTED")) {
            final Status status = Status.valueOf(state);

            return bookingRepository
                    .findAllByBooker_IdAndStatusOrderByStartDesc(bookerId, status)
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("CURRENT")) {
            return bookingRepository
                    .findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                            LocalDateTime.now(), LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("FUTURE")) {
            return bookingRepository.
                    findAllByBooker_IdAndStartAfterOrderByStartDesc(bookerId, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("PAST")) {
            return bookingRepository
                    .findAllByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<BookingResponceDto> getAllBookingForOwner(long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", ownerId)));
        if (state.equals("ALL")) {
            return bookingRepository
                    .findAllByItem_Owner_IdOrderByStartDesc(ownerId)
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("WAITING") || state.equals("REJECTED")) {
            final Status status = Status.valueOf(state);

            return bookingRepository
                    .findAllByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, status)
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("CURRENT")) {
            return bookingRepository
                    .findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                            LocalDateTime.now(), LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("FUTURE")) {
            return bookingRepository.
                    findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else if (state.equals("PAST")) {
            return bookingRepository
                    .findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::map2BookingDto)
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public BookingResponceDto getBookingById(long bookingId, long userId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id = %d not found", bookingId)));

        final Long bookerId = booking.getBooker().getId();
        final Long ownerId = booking.getItem().getOwner().getId();

        if(!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new MismatchException(String.format("user id = %d mismatch", userId));
        }

        return BookingMapper.map2BookingDto(booking);
    }

    @Override
    public BookingResponceDto addNewBooking(BookingRequestDto bookingDto, long userId) {
        bookingDto.setStatus(Status.WAITING);

        if (bookingDto.getEnd().isBefore(LocalDateTime.now())
                || bookingDto.getStart().isBefore(LocalDateTime.now())
                || bookingDto.getStart().isAfter(bookingDto.getEnd())
        ) {
            throw new DateTimeException("DateTime booking entered incorrectly!");
        }

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Item id = %d not found", bookingDto.getItemId())));

        if (item.getOwner().getId().equals(userId)) {
           throw new MismatchException(String.format("Item owner other user"));
        }

        if (!item.isAvailable()) {
            throw new ValidationException(String.format("Booking create from userId = %d to itemId = %d unavailable",
                    userId, item.getId()));
        }
        final Booking booking = BookingMapper.map2Booking(bookingDto, user, item);

        bookingRepository.save(booking);
        return BookingMapper.map2BookingDto(booking);
    }

    @Override
    public BookingResponceDto updateBookingStatus(long bookingId, boolean status, long ownerId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id = %d not found", bookingId)));
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", ownerId)));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new MismatchException(String.format("Item owner other user"));
        }

        if(!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException(String.format("Booking status has already been changed"));
        }

       if(status) {
           booking.setStatus(Status.APPROVED);
       } else {
           booking.setStatus(Status.REJECTED);
       }

       bookingRepository.save(booking);
       return BookingMapper.map2BookingDto(booking);
    }
}
