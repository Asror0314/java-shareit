package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.beans.Transient;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class ItemDtoForRequest {

    private Long id;

    private String name;

    private String description;

    private String available;

    private long requestId;

    @Transient
    public String getStringAvailable() {
        return this.available;
    }

    public boolean getAvailable() {
        return Boolean.valueOf(available);
    }

}
