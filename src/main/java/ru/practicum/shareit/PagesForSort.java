package ru.practicum.shareit;

import javax.validation.ValidationException;

public class PagesForSort {

    public static boolean createPage(String from, String size) {
        if (from != null && size != null) {
            final int fromInt = Integer.parseInt(from);
            final int sizeInt = Integer.parseInt(size);

            if (fromInt < 0) {
                throw new ValidationException("From cannot be less than 0!");
            }
            if (sizeInt < 1) {
                throw new ValidationException("Size cannot be less than 1!");
            }
            return true;
        } else if (from == null && size == null) {
            return false;
        } else {
            throw new ValidationException("Please, enter from and size!");
        }
    }
}
