package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdatedItem;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto save(Item item, long userId);

    ItemDto update(UpdatedItem item, long itemId, long userId);

    ItemDto get(long itemId);

    List<ItemDto> getAllByUserId(long userId);

    List<ItemDto> findByName(String text);
}