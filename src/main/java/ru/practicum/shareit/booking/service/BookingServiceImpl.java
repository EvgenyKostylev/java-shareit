package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.dto.BookingToOwnerDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    public BookingOutDto save(BookingInDto request, long userId) {
        Item item = itemService.getItemById(request.getItemId());
        User user = userService.getUserById(userId);

        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Вещь с id %d не доступна для бронирования", item.getId()));
        }

        return BookingMapper.toBookingOutDto(repository.save(BookingMapper.toBooking(request, item, user)));
    }

    @Transactional
    public BookingToOwnerDto update(long bookingId, boolean approved, long userId) {
        Booking booking = getBookingById(bookingId);

        if (booking.getItem().getOwner().getId() != userId) {
            throw new ForbiddenException(String.format(
                    "Пользователь с id %d не может изменять статус бронирования с id %d",
                    userId,
                    bookingId));
        }

        if (approved) {
            return BookingMapper.updateBookingFields(booking, Status.APPROVED);
        } else {
            return BookingMapper.updateBookingFields(booking, Status.REJECTED);
        }
    }

    public BookingOutDto get(long bookingId, long userId) {
        Booking booking = getBookingById(bookingId);

        if ((booking.getBooker().getId() != userId) && (booking.getItem().getOwner().getId() != userId)) {
            throw new ValidationException(String.format(
                    "Бронирование с id %d не принадлежит пользователю с id %d",
                    bookingId,
                    userId));
        }

        return BookingMapper.toBookingOutDto(booking);
    }

    public List<BookingOutDto> getAllByUserId(State state, long userId) {
        if (state == State.ALL) {
            return repository.findAllByBookerIdOrderByStartDesc(userId)
                    .stream()
                    .map(BookingMapper::toBookingOutDto)
                    .toList();
        }
        return repository.findAllByBookerIdOrderByStartDesc(userId)
                .stream()
                .map(BookingMapper::toBookingOutDto)
                .filter(booking -> booking.getState() == state)
                .toList();
    }

    public List<BookingOutDto> getAllByOwnerId(State state, long userId) {
        if (repository.countByItemOwnerId(userId) <= 1) {
            throw new NotFoundException(String.format("У пользователя с id %d отсутствуют вещи в аренде", userId));
        }

        if (state == State.ALL) {
            return repository.findAllByItemOwnerIdOrderByStartDesc(userId)
                    .stream()
                    .map(BookingMapper::toBookingOutDto)
                    .toList();
        }

        return repository.findAllByItemOwnerIdOrderByStartDesc(userId)
                .stream()
                .map(BookingMapper::toBookingOutDto)
                .filter(booking -> booking.getState() == state)
                .toList();
    }

    private Booking getBookingById(long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Бронирование с id %d не найдено",
                        bookingId)));
    }
}