package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

@Data
@Builder
public class ItemBookingDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private Long ownerId;

    private Long requestId;

    private BookingOutDto lastBooking;

    private BookingOutDto nextBooking;

    private List<CommentDto> comments;
}