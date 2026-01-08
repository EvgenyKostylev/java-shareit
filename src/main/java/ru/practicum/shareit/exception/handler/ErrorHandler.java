package ru.practicum.shareit.exception.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.model.Error;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.ForbiddenException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public Error handleNotFoundError(final NotFoundException e) {
        return new Error("Ошибка данных", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public Error handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        return new Error("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) //403
    public Error handleForbiddenError(final ForbiddenException e) {
        return new Error("Ошибка доступа к данным", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public Error handleValidationError(final ValidationException e) {
        return new Error("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public Error handleMissingRequestHeader(final MissingRequestHeaderException e) {
        return new Error("Отсутствует заголовок 'X-Sharer-User-Id'", e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT) //409
    public Error handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        return new Error("Ошибка валидации", e.getMessage());
    }
}