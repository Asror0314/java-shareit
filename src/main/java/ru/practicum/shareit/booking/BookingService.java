package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponceDto;

import java.util.List;

public interface BookingService {

    List<BookingResponceDto> getAllBooking(long userId);

    BookingResponceDto getBookingById(long bookingDtoId);

    BookingResponceDto addNewBooking(BookingRequestDto bookingDto, long userId);

    BookingResponceDto updateBookingStatus(long bookingId, boolean status, long ownerId);

}
