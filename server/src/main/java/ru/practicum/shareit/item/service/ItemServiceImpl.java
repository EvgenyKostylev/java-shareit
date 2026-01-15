package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Override
    public ItemDto saveItem(ItemDto request, long userId) {
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(
                request,
                userService.getUserById(userId),
                request.getRequestId() != null ?
                        itemRequestService.getItemRequestById(request.getRequestId())
                        : null)));

        log.info("Вещь успешно создана: itemId={}", itemDto.getId());

        return itemDto;
    }

    @Override
    public CommentDto saveComment(CommentDto request, long itemId, long userId) {
        if (!isUserBookingItem(itemId, userId)) {
            throw new ValidationException(String.format(
                    "Пользователь с id %d не бронировал предмет с id %d",
                    userId,
                    itemId));
        }

        CommentDto commentDto = CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(request,
                getItemById(itemId),
                userService.getUserById(userId))));

        log.info("Комментарий вещи itemId={} успешно создан commentId={}", itemId, commentDto.getId());

        return commentDto;
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto item, long itemId, long userId) {
        ownsItem(userId, itemId);

        ItemDto itemDto = ItemMapper.updateItemFields(
                getItemById(itemId),
                item,
                item.getRequestId() != null ? itemRequestService.getItemRequestById(item.getRequestId()) : null);

        log.info("Вещь изменена itemId={}", itemDto.getId());

        return itemDto;
    }

    @Override
    public ItemBookingDto get(long itemId, Long userId) {
        ItemBookingDto itemBookingDto = getItemBookingDto(getItemById(itemId), userId);

        log.info("Вывод вещи itemId={}", itemBookingDto.getId());

        return itemBookingDto;
    }

    @Override
    public List<ItemBookingDto> getAllByUserId(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Item> items = itemRepository.findByOwnerId(userId, pageable);

        log.info("Вывод {} вещей владельца userId={}", items.size(), userId);

        return getItemBookingsDtoByOwner(items);
    }

    @Override
    public List<ItemDto> findByName(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        List<ItemDto> itemsDto = itemRepository
                .findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        text,
                        text)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();

        log.info("Вывод {} вещей по тексту text={}", itemsDto.size(), text);

        return itemsDto;
    }

    @Override
    public void ownsItem(long userId, long itemId) {
        if (getItemById(itemId).getOwner().getId() != userId) {
            throw new ForbiddenException(String.format(
                    "Вещь с id %d не принадлежит пользователю с id %d",
                    itemId,
                    userId
            ));
        }

        log.info("Вещь itemId={} принадлежит пользователю userId={}", itemId, userId);
    }

    @Override
    public Item getItemById(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id %d не найдена", itemId)));

        log.info("Найдена вещь itemId={}", itemId);

        return item;
    }

    private boolean isUserBookingItem(long itemId, long userId) {
        boolean isUserBookingItem = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(
                        itemId,
                        userId,
                        LocalDateTime.now())
                .stream()
                .anyMatch(booking -> booking.getStatus() == Status.APPROVED);

        log.info("Пользователь userId={} бронировал вещь itemId={}: {}", userId, itemId, isUserBookingItem);

        return isUserBookingItem;
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

        ItemBookingDto itemBookingDto = ItemMapper.toItemBookingDto(
                item,
                lastBooking,
                nextBooking,
                commentRepository.findAllByItemId(item.getId()));

        log.info("Найдены бронирования и комментарии вещи itemId={}", item.getId());

        return itemBookingDto;
    }

    private List<ItemBookingDto> getItemBookingsDtoByOwner(List<Item> items) {
        List<Long> itemIds = items.stream().map(Item::getId).toList();
        Map<Long, List<Comment>> commentsByItemIds = getCommentsByItemIds(itemIds);
        Map<Long, Booking> lastBookingsByItemIds = getLastBookingsByItemIds(itemIds);
        Map<Long, Booking> nextBookingsByItemIds = getNextBookingsByItemIds(itemIds);
        List<ItemBookingDto> itemBookingsDto = items.stream()
                .map(item ->
                        ItemMapper.toItemBookingDto(
                                item,
                                lastBookingsByItemIds.get(item.getId()),
                                nextBookingsByItemIds.get(item.getId()),
                                commentsByItemIds.get(item.getId())))
                .toList();

        log.info("Собраны Dto обьекты {} вещей", itemBookingsDto.size());

        return itemBookingsDto;
    }

    private Map<Long, List<Comment>> getCommentsByItemIds(List<Long> itemIds) {
        Map<Long, List<Comment>> commentsByItemIds = commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        log.info("Найдены комментарии {} вещей {}", commentsByItemIds.size(), itemIds.size());

        return commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
    }

    private Map<Long, Booking> getLastBookingsByItemIds(List<Long> itemIds) {
        Map<Long, Booking> lastBookingsByItemIds = bookingRepository.findLastBookings(itemIds, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));

        log.info("Найдены последние бронирования {} вещей {}", lastBookingsByItemIds.size(), itemIds.size());

        return lastBookingsByItemIds;
    }

    private Map<Long, Booking> getNextBookingsByItemIds(List<Long> itemIds) {
        Map<Long, Booking> nextBookingsByItemIds = bookingRepository.findNextBookings(itemIds, LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), Function.identity()));

        log.info("Найдены следующие бронирования {} вещей {}", nextBookingsByItemIds.size(), itemIds.size());

        return nextBookingsByItemIds;
    }
}