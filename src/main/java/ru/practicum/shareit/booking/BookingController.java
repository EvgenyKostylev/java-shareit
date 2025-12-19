package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.dto.BookingToOwnerDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingOutDto save(@Valid @RequestBody BookingInDto request,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.save(request, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingToOwnerDto update(
            @PathVariable("bookingId") long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.update(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto get(
            @PathVariable("bookingId") long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutDto> getAllByUserId(
            @RequestParam(name = "state", defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getAllByUserId(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllByOwnerId(
            @RequestParam(name = "state", defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getAllByOwnerId(state, userId);
    }
}