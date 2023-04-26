package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.DateTimeException;
import ru.practicum.shareit.exception.MismatchException;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.ValidationException;

@RestControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        return new ErrorResponse(String.format("%s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMismatch(MismatchException e) {
        return new ErrorResponse(String.format("%s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDateTime(DateTimeException e) {
        return new ErrorResponse(String.format("%s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationException e) {
        return new ErrorResponse(String.format("%s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final RuntimeException e) {
        return new ErrorResponse(String.format("%s", e.getMessage()));
    }
}
