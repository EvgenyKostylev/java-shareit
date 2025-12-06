package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto save(ItemDto item, long userId);

    ItemDto update(ItemDto item, long itemId, long userId);

    ItemDto get(long itemId);

    List<ItemDto> getAllByUserId(long userId);

    List<ItemDto> findByName(String text);
}