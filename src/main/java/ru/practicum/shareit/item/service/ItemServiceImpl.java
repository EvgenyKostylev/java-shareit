package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public ItemDto saveItem(ItemDto item, long userId) {
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(
                item,
                userService.getUserById(userId),
                null)));
    }

    public CommentDto saveComment(CommentDto comment, long itemId, long userId) {
        if (!isUserBookingItem(itemId, userId)) {
            throw new ValidationException(String.format(
                    "Пользователь с id %d не бронировал предмет с id %d",
                    userId,
                    itemId));
        }

        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(
                comment,
                getItemById(itemId),
                userService.getUserById(userId))));
    }

    @Transactional
    public ItemDto update(ItemDto item, long itemId, long userId) {
        ownsItem(userId, itemId);

        return ItemMapper.updateItemFields(getItemById(itemId), item, null);
    }

    public ItemBookingDto get(long itemId, Long userId) {
        return getItemBookingDto(getItemById(itemId), userId);
    }

    public List<ItemBookingDto> getAllByUserId(long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);

        return getItemBookingsDtoByOwner(items);
    }

    public List<ItemDto> findByName(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        text,
                        text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public void ownsItem(long userId, long itemId) {
        if (getItemById(itemId).getOwner().getId() != userId) {
            throw new ForbiddenException(String.format(
                    "Вещь с id %d не принадлежит пользователю с id %d",
                    itemId,
                    userId
            ));
        }
    }

    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id %d не найдена", itemId)));
    }

    private boolean isUserBookingItem(long itemId, long userId) {
        return bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(
                        itemId,
                        userId,
                        LocalDateTime.now())
                .stream()
                .anyMatch(booking -> booking.getStatus() == Status.APPROVED);
    }

    private ItemBookingDto getItemBookingDto(Item item, Long userId) {
        Booking lastBooking = null;
        Booking nextBooking = null;

        if (userId != null && item.getOwner().getId() == userId) {
            lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(
                    item.getId(),
                    LocalDateTime.now());
            nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(
                    item.getId(),
                    LocalDateTime.now());
        }

        return ItemMapper.toItemBookingDto(
                item,
                lastBooking,
                nextBooking,
                commentRepository.findAllByItemId(item.getId()));
    }

    private List<ItemBookingDto> getItemBookingsDtoByOwner(List<Item> items) {
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<Comment>> commentsByItemIds = getCommentsByItemIds(itemIds);
        Map<Long, Booking> lastBookingsByItemIds = getLastBookingsByItemIds(itemIds);
        Map<Long, Booking> nextBookingsByItemIds = getNextBookingsByItemIds(itemIds);

        return items.stream()
                .map(item ->
                        ItemMapper.toItemBookingDto(
                                item,
                                lastBookingsByItemIds.get(item.getId()),
                                nextBookingsByItemIds.get(item.getId()),
                                commentsByItemIds.get(item.getId())))
                .toList();
    }

    private Map<Long, List<Comment>> getCommentsByItemIds(List<Long> itemIds) {
        return commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
    }

    private Map<Long, Booking> getLastBookingsByItemIds(List<Long> itemIds) {
        return bookingRepository.findLastBookings(itemIds, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
    }

    private Map<Long, Booking> getNextBookingsByItemIds(List<Long> itemIds) {
        return bookingRepository.findNextBookings(itemIds, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));
    }
}