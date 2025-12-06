package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
    private final UserServiceImpl userService;
    private final Map<Long, Item> items;

    public ItemServiceImpl(@Autowired UserServiceImpl userService) {
        this.userService = userService;
        items = new HashMap<>();
    }

    public ItemDto save(ItemDto item, long userId) {
        item.setOwner(UserMapper.toUser(userService.get(userId)));
        item.setId((long) item.hashCode());
        items.put(item.getId(), ItemMapper.toItem(item));

        if (!items.containsKey(item.getId())) {
            throw new ValidationException(String.format("Не удалось добавить вещь: " + item));
        }

        return ItemMapper.toItemDto(items.get(item.getId()));
    }

    public ItemDto update(ItemDto item, long itemId, long userId) {
        userService.get(userId);
        itemExists(itemId);
        ownsItem(userId, itemId);
        items.put(itemId, ItemMapper.updateItemFields(items.get(itemId), item));

        return ItemMapper.toItemDto(items.get(itemId));
    }

    public ItemDto get(long itemId) {
        itemExists(itemId);

        return ItemMapper.toItemDto(items.get(itemId));
    }

    public List<ItemDto> getAllByUserId(long userId) {
        userService.get(userId);

        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public List<ItemDto> findByName(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        (item.getName() != null && item.getName()
                                .toLowerCase()
                                .contains(text.toLowerCase())) ||
                                (item.getDescription() != null && item.getDescription()
                                        .toLowerCase()
                                        .contains(text.toLowerCase())))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private void itemExists(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(String.format("Вещь с id %d не найдена", itemId));
        }
    }

    private void ownsItem(long userId, long itemId) {
        if (items.get(itemId).getOwner().getId() != userId) {
            throw new ValidationException(String.format(
                    "Вещь с id %d не принадлежит пользователю с id %d",
                    itemId,
                    userId
            ));
        }
    }
}