package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto saveItem(@Valid @RequestBody ItemDto request,
                            @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.saveItem(request, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(
            @RequestBody CommentDto request,
            @PathVariable("itemId") long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.saveComment(request, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto request,
                          @PathVariable("itemId") long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.update(request, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto get(
            @PathVariable("itemId") long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return service.get(itemId, userId);
    }

    @GetMapping
    public List<ItemBookingDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getByName(@RequestParam String text) {
        return service.findByName(text);
    }
}