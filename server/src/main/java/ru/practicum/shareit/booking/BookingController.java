package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getAllBookings(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
            ) {
        return bookingService.getAllBookings(userId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForOwner(
            @RequestHeader(value = "X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
            ) {
        return bookingService.getAllBookingsForOwner(userId, stateParam, from, size);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @PathVariable long bookingId,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PostMapping
    public BookingDto addNewBook(
            @Valid @RequestBody BookingRequestDto booking,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return bookingService.addNewBook(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookStatus(
            @PathVariable long bookingId,
            @RequestParam(value = "approved") boolean status,
            @RequestHeader(value = "X-Sharer-User-Id") long userId
    ) {
        return bookingService.updateBookStatus(bookingId, status, userId);
    }
}
