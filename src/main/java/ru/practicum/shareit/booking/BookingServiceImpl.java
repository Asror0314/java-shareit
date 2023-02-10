package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponceDto;
import ru.practicum.shareit.exception.DateTimeException;
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
    public List<BookingResponceDto> getAllBooking(long userId) {
        return bookingRepository.findAll()
                .stream()
                .map(BookingMapper::map2BookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponceDto getBookingById(long bookingId) {
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking id = %d not found", bookingId)));

        return BookingMapper.map2BookingDto(booking);
    }

    @Override
    public BookingResponceDto addNewBooking(BookingRequestDto bookingDto, long userId) {
        bookingDto.setStatus(Status.WAITING);

        if (bookingDto.getEnd().isBefore(LocalDateTime.now())
                || bookingDto.getStart().isBefore(LocalDateTime.now())
                || bookingDto.getStart().isAfter(bookingDto.getEnd())
        ) {
            throw new DateTimeException("DateTime booking entered incorrectly");
        }

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", userId)));
        final Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Item id = %d not found", bookingDto.getItemId())));

        if (item.getOwner().getId().equals(userId)) {
           throw new ValidationException(String.format("Item owner other user"));
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
        final User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User id = %d not found", ownerId)));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException(String.format("Item owner other user"));
        }

       if(status) {
           booking.setStatus(Status.APPROVED);
       } else {
           booking.setStatus(Status.REJECTED);
       }

        return BookingMapper.map2BookingDto(booking);
    }
}
