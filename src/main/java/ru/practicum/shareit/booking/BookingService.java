package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    List<BookingDto> getAllBooking(long bookerId, String state);

    List<BookingDto> getAllBookingForOwner(long ownerId, String state);

    BookingDto getBookingById(long bookingDtoId, long userId);

    BookingDto addNewBooking(BookingRequestDto bookingDto, long userId);

    BookingDto updateBookingStatus(long bookingId, boolean status, long ownerId);

}
