package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.beans.Transient;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
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
