package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(ItemRequestDto request, long userId);

    ItemRequestDto get(long userId);

    List<ItemRequestDto> getAll();

    List<ItemRequestDto> getUserRequests(long userId);

    ItemRequest getItemRequestById(long itemRequestId);
}