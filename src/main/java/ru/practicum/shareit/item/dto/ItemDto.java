package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String description;

    @NotNull
    private String available;

    private BookingItemDto lastBooking;

    private BookingItemDto nextBooking;

    private List<CommentDto> comments;


    @Transient
    public String getStringAvailable() {
        return available;
    }

    public boolean getAvailable() {
        return Boolean.valueOf(available);
    }
}
