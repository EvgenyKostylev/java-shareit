package ru.practicum.shareit.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.Error;
import ru.practicum.shareit.exception.ValidationException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleNotFoundError(final NotFoundException e) {
        return new Error("Ошибка данных", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        return new Error("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error handleValidationError(final ValidationException e) {
        return new Error("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleMissingRequestHeader(final MissingRequestHeaderException e) {
        return new Error("Отсутствует заголовок 'X-Sharer-User-Id'", e.getMessage());
    }
}