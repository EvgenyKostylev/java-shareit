package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestBody @Valid ItemDto request,
                                           @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Creating item {}, userId={}", request, userId);

        return itemClient.saveItem(request, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestBody @Valid CommentDto request,
                                              @PathVariable("itemId") @Positive long itemId,
                                              @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Creating comment {}, itemId={}, userId={}", request, itemId, userId);

        return itemClient.saveComment(request, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto request,
                                         @PathVariable("itemId") @Positive long itemId,
                                         @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Updating item {}, itemId={}, userId={}", request, itemId, userId);

        return itemClient.update(request, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable("itemId") @Positive long itemId,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) long userId) {
        log.info("Get item {}, userId={}", itemId, userId);

        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Get items for user {}", userId);

        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestParam("text") @NotBlank String text) {
        log.info("Get items by text {}", text);

        return itemClient.getItemsByText(text);
    }
}