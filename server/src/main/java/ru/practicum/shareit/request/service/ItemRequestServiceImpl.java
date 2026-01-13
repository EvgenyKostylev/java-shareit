package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto save(ItemRequestDto request, long userId) {
        return ItemRequestMapper.toItemRequestDto(repository.save(ItemRequestMapper.toItemRequest(
                request,
                userService.getUserById(userId))), null);
    }

    @Override
    public ItemRequestDto get(long requestId) {
        return ItemRequestMapper.toItemRequestDto(getItemRequestById(requestId),
                itemRepository.findByRequestId(requestId));
    }

    @Override
    public List<ItemRequestDto> getAll() {
        List<ItemRequest> itemRequests = repository.findAllByOrderByCreatedDesc();

        return itemRequests.stream().map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest,
                null)).toList();
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        userService.getUserById(userId);

        List<ItemRequest> itemRequests = repository.findByRequestorIdOrderByCreatedDesc(userId);

        return getItemRequestsDtoByItemRequests(itemRequests);
    }

    @Override
    public ItemRequest getItemRequestById(long itemRequestId) {
        return repository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Запрос на вещь с id %d не найден",
                        itemRequestId)));
    }

    private Map<Long, List<Item>> getItemsByItemRequestIds(List<Long> itemRequestIds) {
        return itemRepository.findByRequestIdIn(itemRequestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
    }

    private List<ItemRequestDto> getItemRequestsDtoByItemRequests(List<ItemRequest> itemRequests) {
        List<Long> itemRequestIds = itemRequests.stream().map(ItemRequest::getId).toList();
        Map<Long, List<Item>> itemsByItemRequestIds = getItemsByItemRequestIds(itemRequestIds);

        return itemRequests.stream()
                .map(itemRequest ->
                        ItemRequestMapper.toItemRequestDto(
                                itemRequest,
                                itemsByItemRequestIds.get(itemRequest.getId())))
                .toList();
    }
}