package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.dto.BookingToOwnerDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingOutDto save(BookingInDto request, long userId);

    BookingToOwnerDto update(long bookingId, boolean approved, long userId);

    BookingOutDto get(long bookingId, long userId);

    List<BookingOutDto> getAllByUserId(State state, long userId);

    List<BookingOutDto> getAllByOwnerId(State state, long userId);
}