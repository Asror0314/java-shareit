package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static BookingDto map2BookingDto(Booking booking) {
        final BookingDto bookingDto = new BookingDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setItem(ItemMapper.map2ItemDto(booking.getItem()));

        return bookingDto;
    }

    public static Booking map2Booking(BookingRequestDto bookingDto, User user, Item item) {
        final Booking booking = new Booking();

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        booking.setBooker(user);
        booking.setItem(item);
        return booking;
    }

    public static BookingItemDto map2BookingItemDto(Booking booking) {
        final BookingItemDto bookingItemDto = new BookingItemDto();

        bookingItemDto.setId(booking.getId());
        bookingItemDto.setBookerId(booking.getBooker().getId());

        return bookingItemDto;
    }
}
