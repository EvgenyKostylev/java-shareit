package ru.practicum.shareit.request.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private long id;

    private long requestor;

    private String description;

    private LocalDateTime created;
}