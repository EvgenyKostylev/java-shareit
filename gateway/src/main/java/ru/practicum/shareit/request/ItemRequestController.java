package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid ItemRequestDto request,
                                       @RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Creating itemRequest {}, userId={}", request, userId);

        return itemRequestClient.save(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Get userItemRequests for user {}", userId);

        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests() {
        log.info("Get all itemRequests");

        return itemRequestClient.getItemRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable("requestId") @Positive long requestId) {
        log.info("Get itemRequest {}", requestId);

        return itemRequestClient.getItemRequest(requestId);
    }
}