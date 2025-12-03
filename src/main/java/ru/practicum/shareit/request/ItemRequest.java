package ru.practicum.shareit.request;

import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private long id;

    private long userId;

    private String description;
}