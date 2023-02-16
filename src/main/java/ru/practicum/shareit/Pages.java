package ru.practicum.shareit;

import javax.validation.ValidationException;

public class Pages {

    public static boolean createPage(String from, String size) {
        if (from != null && size != null) {
            final int fromInt = Integer.valueOf(from);
            final int sizeInt = Integer.valueOf(size);

            if (fromInt < 0) {
                throw new ValidationException(String.format("From cannot be less than 0!"));
            }
            if (sizeInt < 1) {
                throw new ValidationException(String.format("Size cannot be less than 1!"));
            }
            return true;
        } else if (from == null && size == null) {
            return false;
        } else {
            throw new ValidationException(String.format("Please, enter from and size!"));
        }
    }
}
