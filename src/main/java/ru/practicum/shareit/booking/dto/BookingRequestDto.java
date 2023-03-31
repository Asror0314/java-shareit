package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    private long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private Status status;
}
