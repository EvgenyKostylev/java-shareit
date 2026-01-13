package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        log.info("Get booking with state {}, userId={}", stateParam, userId);

        return bookingClient.getBookings(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid BookItemRequestDto request,
                                       @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Creating booking {}, userId={}", request, userId);

        return bookingClient.save(userId, request);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable("bookingId") @Positive long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Get booking {}, userId={}", bookingId, userId);

        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@PathVariable("bookingId") @Positive long bookingId,
                                         @RequestParam @Positive boolean approved,
                                         @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Updating booking {}, approved={}, userId={}", bookingId, approved, userId);

        return bookingClient.update(bookingId, approved, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestParam(name = "state", defaultValue = "ALL") @NotBlank String stateParam,
            @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        log.info("Get owner bookings with state {}, userId={}", state, userId);

        return bookingClient.getOwnerBookings(state, userId);
    }
}