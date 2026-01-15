package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
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

        BookingOutDto bookingOutDto = BookingMapper.toBookingOutDto(repository.save(BookingMapper.toBooking(
                request,
                item,
                user)));

        log.info("Бронирование успешно создано: bookingId={}", bookingOutDto.getId());

        return bookingOutDto;
    }

    @Override
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

        BookingOutDto bookingOutDto;

        if (approved) {
            bookingOutDto = BookingMapper.updateBookingFields(booking, Status.APPROVED);
        } else {
            bookingOutDto = BookingMapper.updateBookingFields(booking, Status.REJECTED);
        }

        log.info("Статус бронирования bookingId={} изменен на Status={}", booking.getId(), bookingOutDto.getStatus());

        return bookingOutDto;
    }

    @Override
    public BookingOutDto get(long bookingId, long userId) {
        Booking booking = getBookingById(bookingId);

        if ((booking.getBooker().getId() != userId) && (booking.getItem().getOwner().getId() != userId)) {
            throw new ValidationException(String.format(
                    "Бронирование с id %d не принадлежит пользователю с id %d",
                    bookingId,
                    userId));
        }

        log.info("Вывод бронирования bookingId={}", bookingId);

        return BookingMapper.toBookingOutDto(booking);
    }

    @Override
    public List<BookingOutDto> getAllByUserId(State state, long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Booking> bookings = getBookingsByBookerAndState(userId, state, pageable);

        log.info("Вывод {} бронирований пользователя userId={}", bookings.size(), userId);

        return bookings.stream().map(BookingMapper::toBookingOutDto).toList();
    }

    @Override
    public List<BookingOutDto> getAllByOwnerId(State state, long userId, int from, int size) {
        if (repository.countByItemOwnerId(userId) <= 1) {
            throw new NotFoundException(String.format("У пользователя с id %d отсутствуют вещи в аренде", userId));
        }

        Pageable pageable = PageRequest.of(from, size);
        List<Booking> bookings = getBookingsByOwnerAndState(userId, state, pageable);

        log.info("Вывод {} бронирований владельца userId={}", bookings.size(), userId);

        return bookings.stream().map(BookingMapper::toBookingOutDto).toList();
    }

    private Booking getBookingById(long bookingId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Бронирование с id %d не найдено",
                        bookingId)));

        log.info("Найдено бронирование bookingId={}", booking.getId());

        return booking;
    }

    private List<Booking> getBookingsByOwnerAndState(long ownerId, State state, Pageable pageable) {
        List<Booking> bookings = switch (state) {
            case CURRENT -> repository.findAllByItemOwnerIdAndContainingDateOrderByStartDesc(
                    ownerId,
                    LocalDateTime.now(),
                    Status.APPROVED,
                    pageable);
            case PAST -> repository.findAllByItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(
                    ownerId,
                    Status.APPROVED,
                    LocalDateTime.now(),
                    pageable);
            case FUTURE -> repository.findAllByItemOwnerIdAndStatusAndEndBeforeOrderByStartDesc(
                    ownerId,
                    Status.APPROVED,
                    LocalDateTime.now(),
                    pageable);
            case WAITING -> repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId,
                    Status.WAITING,
                    pageable);
            case REJECTED -> repository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                    ownerId,
                    Status.REJECTED,
                    pageable);
            default -> repository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable);
        };

        log.info("Найдено {} статуса state={} бронирований владельца userId={}", bookings.size(), state, ownerId);

        return bookings;
    }

    private List<Booking> getBookingsByBookerAndState(long bookerId, State state, Pageable pageable) {
        List<Booking> bookings = switch (state) {
            case CURRENT -> repository.findAllByBookerIdAndContainingDateOrderByStartDesc(
                    bookerId,
                    LocalDateTime.now(),
                    Status.APPROVED,
                    pageable);
            case PAST -> repository.findAllByBookerIdAndStatusAndStartAfterOrderByStartDesc(
                    bookerId,
                    Status.APPROVED,
                    LocalDateTime.now(),
                    pageable);
            case FUTURE -> repository.findAllByBookerIdAndStatusAndEndBeforeOrderByStartDesc(
                    bookerId,
                    Status.APPROVED,
                    LocalDateTime.now(),
                    pageable);
            case WAITING -> repository.findAllByBookerIdAndStatusOrderByStartDesc(
                    bookerId,
                    Status.WAITING,
                    pageable);
            case REJECTED -> repository.findAllByBookerIdAndStatusOrderByStartDesc(
                    bookerId,
                    Status.REJECTED,
                    pageable);
            default -> repository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
        };

        log.info("Найдено {} статута state={} бронирований пользователя userId={}", bookings.size(), state, bookerId);

        return bookings;
    }
}