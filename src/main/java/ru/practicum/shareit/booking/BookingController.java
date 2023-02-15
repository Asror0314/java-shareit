package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingDto> getAllBooking(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state
            ) {
        return bookingService.getAllBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingForOwner(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state
            ) {
        return bookingService.getAllBookingForOwner(userId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @PathVariable long bookingId,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto addNewBooking(
            @Valid @RequestBody BookingRequestDto booking,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return bookingService.addNewBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(
            @PathVariable long bookingId,
            @RequestParam(value = "approved") boolean status,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return bookingService.updateBookingStatus(bookingId, status, userId);
    }
}
