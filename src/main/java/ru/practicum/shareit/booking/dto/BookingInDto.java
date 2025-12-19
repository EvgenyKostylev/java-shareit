package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.exception.DateTimeStrictRange;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@DateTimeStrictRange(from = "start", to = "end")
public class BookingInDto {
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}