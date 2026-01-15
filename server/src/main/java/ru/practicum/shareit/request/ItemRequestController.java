package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto save(@RequestBody ItemRequestDto request,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return service.save(request, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "from") int from,
            @RequestParam(name = "size") int size) {
        return service.getUserRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@PathVariable("requestId") long requestId) {
        return service.get(requestId);
    }
}