package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemBookingDto toItemBookingDto(
            Item item,
            Booking lastBooking,
            Booking nextBooking,
            List<Comment> comments) {
        return new ItemBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking != null ? BookingMapper.toBookingOutDto(lastBooking) : null,
                nextBooking != null ? BookingMapper.toBookingOutDto(nextBooking) : null,
                comments.stream().map(CommentMapper::toCommentDto).toList()
        );
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setRequest(itemRequest);

        return item;
    }

    public static ItemDto updateItemFields(Item item, ItemDto updatedItem, ItemRequest itemRequest) {
        if (updatedItem.hasName()) {
            item.setName(updatedItem.getName());
        }
        if (updatedItem.hasDescription()) {
            item.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.hasAvailable()) {
            item.setAvailable(updatedItem.getAvailable());
        }
        if (updatedItem.hasRequest()) {
            item.setRequest(itemRequest);
        }

        return toItemDto(item);
    }
}