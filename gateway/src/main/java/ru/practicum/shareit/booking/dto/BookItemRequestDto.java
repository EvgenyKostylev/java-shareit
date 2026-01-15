package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.annotation.DateTimeStrictRange;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@DateTimeStrictRange(from = "start", to = "end")
public class BookItemRequestDto {
    private long itemId;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
}