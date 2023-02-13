package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponceDto;

import java.util.List;

public interface BookingService {

    List<BookingResponceDto> getAllBooking(long bookerId, String state);

    List<BookingResponceDto> getAllBookingForOwner(long ownerId, String state);

    BookingResponceDto getBookingById(long bookingDtoId, long userId);

    BookingResponceDto addNewBooking(BookingRequestDto bookingDto, long userId);

    BookingResponceDto updateBookingStatus(long bookingId, boolean status, long ownerId);

}
