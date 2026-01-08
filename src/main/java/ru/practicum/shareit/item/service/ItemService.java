package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(ItemDto item, long userId);

    CommentDto saveComment(CommentDto commentDto, long userId, long commentId);

    ItemDto update(ItemDto item, long itemId, long userId);

    ItemBookingDto get(long itemId, Long userId);

    List<ItemBookingDto> getAllByUserId(long userId);

    List<ItemDto> findByName(String text);

    Item getItemById(long itemId);

    void ownsItem(long userId, long itemId);
}