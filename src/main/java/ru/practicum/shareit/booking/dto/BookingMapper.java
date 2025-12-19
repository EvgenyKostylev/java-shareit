package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class BookingMapper {
    public static BookingOutDto toBookingOutDto(Booking booking) {
        BookingOutDto bookingDto = new BookingOutDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));

        switch (booking.getStatus()) {
            case WAITING:
                bookingDto.setState(State.WAITING);
                break;
            case APPROVED:
                LocalDateTime now = LocalDateTime.now();

                if (now.isBefore(booking.getStart())) {
                    bookingDto.setState(State.FUTURE);
                } else if (now.isAfter(booking.getEnd())) {
                    bookingDto.setState(State.PAST);
                } else {
                    bookingDto.setState(State.CURRENT);
                }

                break;
            case REJECTED:
                bookingDto.setState(State.REJECTED);
                break;
            default:
                throw new ValidationException("Ошибка данных.");
        }

        return bookingDto;
    }

    public static BookingToOwnerDto toBookingToOwnerDto(Booking booking) {
        BookingToOwnerDto bookingDto = new BookingToOwnerDto();

        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    public static Booking toBooking(BookingInDto bookingDto, Item item, User user) {
        Booking booking = new Booking();

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);

        return booking;
    }

    public static BookingToOwnerDto updateBookingFields(Booking booking, Status status) {
        booking.setStatus(status);

        return toBookingToOwnerDto(booking);
    }
}