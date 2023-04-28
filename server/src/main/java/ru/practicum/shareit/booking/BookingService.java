package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    List<BookingDto> getAllBookings(long bookerId, String state, int from, int size);

    List<BookingDto> getAllBookingsForOwner(long ownerId, String state, int from, int size);

    BookingDto getBookingById(long bookingDtoId, Long userId);

    BookingDto addNewBook(BookingRequestDto bookingDto, Long userId);

    BookingDto updateBookStatus(long bookingId, boolean status, Long ownerId);

}
