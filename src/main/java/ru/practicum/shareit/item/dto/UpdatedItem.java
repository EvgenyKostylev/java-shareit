package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

public record UpdatedItem(long id, String name, String description, Boolean available, User owner,
                          ItemRequest request) {
    public boolean hasName() {
        return name != null && !name.isEmpty();
    }

    public boolean hasDescription() {
        return description != null && !description.isEmpty();
    }

    public boolean hasAvailable() {
        return available != null;
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public boolean hasRequest() {
        return request != null;
    }
}