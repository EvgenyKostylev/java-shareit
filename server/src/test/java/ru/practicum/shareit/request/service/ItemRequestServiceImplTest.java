package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class ItemRequestServiceImplTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private UserDto user;

    @BeforeAll
    public void beforeAll() {
        UserDto userDto = UserDto.builder().name("name").email("email").build();

        user = userService.save(userDto);
    }

    @BeforeEach
    public void beforeEach() {
        itemRequestRepository.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testSaveItemRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .build();
        ItemRequestDto itemRequestSave = itemRequestService.save(itemRequestDto, user.getId());

        assertNotNull(itemRequestSave.getId());
        assertEquals("description", itemRequestSave.getDescription());
    }

    @Test
    public void testGetItemRequestDtoById() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .build();
        ItemRequestDto itemRequestSave = itemRequestService.save(itemRequestDto, user.getId());
        ItemRequestDto itemRequestGet = itemRequestService.get(itemRequestSave.getId());

        assertEquals(itemRequestSave.getId(), itemRequestGet.getId());
        assertEquals(itemRequestSave.getDescription(), itemRequestGet.getDescription());
        assertEquals(itemRequestSave.getRequestor(), itemRequestGet.getRequestor());
    }

    @Test
    public void testGetAllItemRequests() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("first description")
                .build();

        itemRequestService.save(itemRequestDto, user.getId());
        itemRequestDto.setDescription("second description");
        itemRequestService.save(itemRequestDto, user.getId());

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getAll();

        assertEquals(2, itemRequestsDto.size());
    }

    @Test
    public void testGetUserItemRequests() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("first description")
                .build();

        itemRequestService.save(itemRequestDto, user.getId());
        itemRequestDto.setDescription("second description");
        itemRequestService.save(itemRequestDto, user.getId());

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getUserRequests(user.getId(), 0, 10);

        assertEquals(2, itemRequestsDto.size());
        assertEquals(user.getId(), itemRequestsDto.get(0).getRequestor());
        assertEquals(user.getId(), itemRequestsDto.get(1).getRequestor());
    }

    @Test
    public void testGetItemRequestById() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("description")
                .build();
        ItemRequestDto itemRequestSave = itemRequestService.save(itemRequestDto, user.getId());
        ItemRequest itemRequestGet = itemRequestService.getItemRequestById(itemRequestSave.getId());

        assertEquals(itemRequestSave.getId(), itemRequestGet.getId());
        assertEquals(itemRequestSave.getDescription(), itemRequestGet.getDescription());
        assertEquals(itemRequestSave.getRequestor(), itemRequestGet.getRequestor().getId());
    }
}