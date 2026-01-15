package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.model.Error;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    public Error handleNotFoundError(final NotFoundException e) {
        log.warn("Произошла ошибка даных: {}", e.getMessage());

        return new Error("Ошибка данных", e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) //403
    public Error handleForbiddenError(final ForbiddenException e) {
        log.warn("Произошла ошибка доступа к данным: {}", e.getMessage());

        return new Error("Ошибка доступа к данным", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    public Error handleValidationError(final ValidationException e) {
        log.warn("Произошла валидации: {}", e.getMessage());

        return new Error("Ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT) //409
    public Error handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        log.warn("Произошла ошибка валидации: {}", e.getMessage());

        return new Error("Ошибка валидации", e.getMessage());
    }
}