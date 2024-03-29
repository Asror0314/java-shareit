package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.beans.Transient;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private String available;

    private BookingItemDto lastBooking;

    private BookingItemDto nextBooking;

    private List<CommentDto> comments;

    private long requestId;

    @Transient
    public String getStringAvailable() {
        return this.available;
    }

    public long getRequestId() {
        return this.requestId;
    }

    public boolean getAvailable() {
        return Boolean.valueOf(available);
    }
}
