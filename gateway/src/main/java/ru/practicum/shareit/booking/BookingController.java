package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getAllBookings(
				 @RequestHeader("X-Sharer-User-Id") long userId,
				 @RequestParam(name = "state", defaultValue = "all") String stateParam,
				 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
				 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getAllBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsForOwner(
			@RequestHeader(value = "X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking for owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getAllBookingsForOwner(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> addNewBook(@RequestHeader("X-Sharer-User-Id") long userId,
											 @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.addNewBook(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
												 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBookStatus(
			@PathVariable long bookingId,
			@RequestParam(name = "approved") boolean status,
			@RequestHeader(value = "X-Sharer-User-Id") long userId
	) {
		return bookingClient.updateBookStatus(bookingId, status, userId);
	}
}
