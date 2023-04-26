package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
public class ItemRequestDto {

    private long id;

    @NotBlank
    private String description;

    private LocalDateTime created;

    private List<ItemDtoForRequest> items;
}
