package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private long id;

    private long requestor;

    private String description;

    private LocalDateTime created;
}