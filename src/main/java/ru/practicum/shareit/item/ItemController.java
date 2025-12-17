package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    @PostMapping
    public ItemDto save(@Valid @RequestBody ItemDto request,
                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.save(request, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto request,
                          @PathVariable("itemId") long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.update(request, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable("itemId") long itemId) {
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getByName(@RequestParam String text) {
        return itemService.findByName(text);
    }
}