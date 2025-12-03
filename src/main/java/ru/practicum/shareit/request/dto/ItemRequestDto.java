package ru.practicum.shareit.request.dto;

import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private long id;

    private long userId;

    private String description;
}