package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
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

import java.time.LocalDateTime;
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

        if (item.getOwner().getId() == userId) {
            throw new ValidationException(String.format(
                    "Вещь (id: %d) не может быть забронирована владельцем (id: %d)",
                    item.getId(),
                    userId));
        }

        if (repository.existsOverlappingBookings(item.getId(), request.getStart(), request.getEnd())) {
            throw new ValidationException(String.format(
                    "Бронирование вещи с id %d пересекается с уже имеющимся",
                    item.getId()));
        }

        if (!item.getAvailable()) {
            throw new ValidationException(String.format(
                    "Вещь с id %d не доступна для бронирования",
                    item.getId()));
        }

        return BookingMapper.toBookingOutDto(repository.save(BookingMapper.toBooking(request, item, user)));
    }

    @Transactional
    public BookingOutDto update(long bookingId, boolean approved, long userId) {
        Booking booking = getBookingById(bookingId);

        if (booking.getItem().getOwner().getId() != userId) {
            throw new ForbiddenException(String.format(
                    "Пользователь с id %d не может изменять статус бронирования с id %d",
                    userId,
                    bookingId));
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new ForbiddenException(String.format("Нельзя изменить статус бронирования с id %d", bookingId));
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
        return getBookingsByBookerAndState(userId, state).stream().map(BookingMapper::toBookingOutDto).toList();
    }

    public List<BookingOutDto> getAllByOwnerId(State state, long userId) {
        if (repository.countByItemOwnerId(userId) <= 1) {
            throw new NotFoundException(String.format("У пользователя с id %d отсутствуют вещи в аренде", userId));
        }

        return getBookingsByOwnerAndState(userId, state).stream().map(BookingMapper::toBookingOutDto).toList();
    }

    private Booking getBookingById(long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Бронирование с id %d не найдено",
                        bookingId)));
    }

    private List<Booking> getBookingsByOwnerAndState(long ownerId, State state) {
        return switch (state) {
            case CURRENT -> repository.findAllByItemOwnerIdAndContainingDateOrderByStartDesc(
                    ownerId,
                    LocalDateTime.now(),
                    Status.APPROVED);
            case PAST -> repository.findAllByItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(
                    ownerId,
                    Status.APPROVED,
                    LocalDateTime.now());
            case FUTURE -> repository.findAllByItemOwnerIdAndStatusAndEndBeforeOrderByStartDesc(
                    ownerId,
                    Status.APPROVED,
                    LocalDateTime.now());
            case WAITING -> repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId,
                    Status.WAITING);
            case REJECTED -> repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId,
                    Status.REJECTED);
            default -> repository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
        };
    }

    private List<Booking> getBookingsByBookerAndState(long bookerId, State state) {
        return switch (state) {
            case CURRENT -> repository.findAllByBookerIdAndContainingDateOrderByStartDesc(
                    bookerId,
                    LocalDateTime.now(),
                    Status.APPROVED);
            case PAST -> repository.findAllByBookerIdAndStatusAndStartAfterOrderByStartDesc(
                    bookerId,
                    Status.APPROVED,
                    LocalDateTime.now());
            case FUTURE -> repository.findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
                    bookerId,
                    Status.APPROVED,
                    LocalDateTime.now());
            case WAITING -> repository.findAllByBookerIdAndStatusOrderByStartDesc(
                    bookerId,
                    Status.WAITING);
            case REJECTED -> repository.findAllByBookerIdAndStatusOrderByStartDesc(
                    bookerId,
                    Status.REJECTED);
            default -> repository.findAllByBookerIdOrderByStartDesc(bookerId);
        };
    }
}