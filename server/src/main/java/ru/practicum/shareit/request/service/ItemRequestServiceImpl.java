package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto save(ItemRequestDto request, long userId) {
        ItemRequestDto itemRequestDto = ItemRequestMapper
                .toItemRequestDto(
                        repository.save(ItemRequestMapper.toItemRequest(
                                request,
                                userService.getUserById(userId))),
                        null);

        log.info("Запрос успешно создан: itemRequestId={}", itemRequestDto.getId());

        return itemRequestDto;
    }

    @Override
    public ItemRequestDto get(long requestId) {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(getItemRequestById(requestId),
                itemRepository.findByRequestId(requestId));

        log.info("Вывод запроса itemRequestId={}", itemRequestDto.getId());

        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAll() {
        List<ItemRequestDto> itemRequestsDto = repository.findAllByOrderByCreatedDesc()
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest,
                        null))
                .toList();

        log.info("Вывод {} запросов", itemRequestsDto.size());

        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId, int from, int size) {
        userService.getUserById(userId);

        Pageable pageable = PageRequest.of(from, size);
        List<ItemRequestDto> itemRequestsDto = getItemRequestsDtoByItemRequests(repository
                .findByRequestorIdOrderByCreatedDesc(
                        userId,
                        pageable));

        log.info("Вывод запросов itemRequests={} пользователем userId={}", itemRequestsDto.size(), userId);

        return itemRequestsDto;
    }

    @Override
    public ItemRequest getItemRequestById(long itemRequestId) {
        ItemRequest itemRequest = repository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Запрос на вещь с id %d не найден",
                        itemRequestId)));

        log.info("Найден запрос itemRequestId={}", itemRequestId);


        return itemRequest;
    }

    private Map<Long, List<Item>> getItemsByItemRequestIds(List<Long> itemRequestIds) {
        Map<Long, List<Item>> itemsByRequestId = itemRepository.findByRequestIdIn(itemRequestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        log.info("Найдены вещи {} заросов {}", itemsByRequestId.size(), itemRequestIds.size());

        return itemsByRequestId;
    }

    private List<ItemRequestDto> getItemRequestsDtoByItemRequests(List<ItemRequest> itemRequests) {
        List<Long> itemRequestIds = itemRequests.stream().map(ItemRequest::getId).toList();
        Map<Long, List<Item>> itemsByItemRequestIds = getItemsByItemRequestIds(itemRequestIds);
        List<ItemRequestDto> itemRequestsDto = itemRequests.stream()
                .map(itemRequest ->
                        ItemRequestMapper.toItemRequestDto(
                                itemRequest,
                                itemsByItemRequestIds.get(itemRequest.getId())))
                .toList();

        log.info("Собраны Dto обьекты {} запросов", itemRequestsDto.size());

        return itemRequestsDto;
    }
}