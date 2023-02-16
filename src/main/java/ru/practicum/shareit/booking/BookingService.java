package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    List<BookingDto> getAllBooking(long bookerId, String state, String from, String size);

    List<BookingDto> getAllBookingForOwner(long ownerId, String state, String from, String size);

    BookingDto getBookingById(long bookingDtoId, long userId);

    BookingDto addNewBooking(BookingRequestDto bookingDto, long userId);

    BookingDto updateBookingStatus(long bookingId, boolean status, long ownerId);

}
