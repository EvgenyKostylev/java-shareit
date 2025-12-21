package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

@Data
@Builder
public class ItemBookingDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private Long requestId;

    private BookingOutDto lastBooking;

    private BookingOutDto nextBooking;

    private List<CommentDto> comments;
}