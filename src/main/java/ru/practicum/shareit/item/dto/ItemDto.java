package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
public record ItemDto(long id, String name, String description, boolean available, User owner, ItemRequest request) {
}