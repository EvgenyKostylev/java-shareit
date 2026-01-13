package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private static UserDto user;
    private static UserDto commentingUser;

    @BeforeAll
    public void beforeAll() {
        UserDto userDto = UserDto.builder().name("name").email("email").build();

        user = userService.save(userDto);
        userDto.setName("commenting name");
        userDto.setEmail("commenting email");
        commentingUser = userService.save(userDto);
    }

    @BeforeEach
    public void beforeEach() {
        itemRepository.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testSaveItem() {
        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();
        ItemDto itemSave = itemService.saveItem(itemDto, user.getId());

        assertNotNull(itemSave.getId());
        assertEquals("name", itemSave.getName());
        assertEquals("description", itemSave.getDescription());
        assertEquals(true, itemSave.getAvailable());
    }

    @Test
    public void testSaveComment() {
        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();
        ItemDto itemSave = itemService.saveItem(itemDto, user.getId());
        BookingInDto bookingInDto = new BookingInDto();

        bookingInDto.setItemId(itemSave.getId());
        bookingInDto.setStart(LocalDateTime.now().minusDays(2L));
        bookingInDto.setEnd(LocalDateTime.now().minusDays(1L));

        BookingOutDto bookingOutDto = bookingService.save(bookingInDto, commentingUser.getId());

        bookingService.update(bookingOutDto.getId(), true, user.getId());

        CommentDto commentDto = CommentDto.builder().text("text").build();
        CommentDto commentSave = itemService.saveComment(commentDto, itemSave.getId(), commentingUser.getId());

        assertNotNull(commentSave.getId());
        assertEquals("text", commentSave.getText());
        assertEquals("commenting name", commentSave.getAuthorName());
    }

    @Test
    public void testUpdateItem() {
        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();
        ItemDto itemSave = itemService.saveItem(itemDto, user.getId());

        itemSave.setName("updated name");

        ItemDto itemUpdate = itemService.update(itemSave, itemSave.getId(), user.getId());

        assertEquals(itemSave.getId(), itemUpdate.getId());
        assertEquals(itemSave.getName(), itemUpdate.getName());
    }

    @Test
    public void testGetItemBookingById() {
        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();
        ItemDto itemSave = itemService.saveItem(itemDto, user.getId());
        ItemBookingDto itemBookingGet = itemService.get(itemSave.getId(), user.getId());

        assertEquals(itemSave.getName(), itemBookingGet.getName());
        assertEquals(itemSave.getDescription(), itemBookingGet.getDescription());
        assertEquals(itemSave.getAvailable(), itemBookingGet.getAvailable());
    }

    @Test
    public void testGetItemsByUserId() {
        ItemDto itemDto = ItemDto.builder()
                .name("first name")
                .description("first description")
                .available(true)
                .build();

        itemService.saveItem(itemDto, user.getId());
        itemDto.setName("second name");
        itemService.saveItem(itemDto, user.getId());

        List<ItemBookingDto> itemBookingsDto = itemService.getAllByUserId(user.getId());

        assertEquals(2, itemBookingsDto.size());
    }

    @Test
    public void testFindItemByName() {
        ItemDto itemDto = ItemDto.builder().name("unknown").description("unknown").available(true).build();

        itemService.saveItem(itemDto, user.getId());
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemService.saveItem(itemDto, user.getId());

        List<ItemDto> itemsDto = itemService.findByName("name");

        assertEquals(1, itemsDto.size());
        assertEquals(itemDto.getName(), itemsDto.getFirst().getName());
    }

    @Test
    public void testUserOwnsItem() {
        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();
        ItemDto itemSave = itemService.saveItem(itemDto, user.getId());

        assertDoesNotThrow(() -> itemService.ownsItem(user.getId(), itemSave.getId()));
    }

    @Test
    public void testGetItemById() {
        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();
        ItemDto itemSave = itemService.saveItem(itemDto, user.getId());
        Item itemGet = itemService.getItemById(itemSave.getId());

        assertEquals(itemSave.getName(), itemGet.getName());
        assertEquals(itemSave.getDescription(), itemGet.getDescription());
        assertEquals(itemSave.getAvailable(), itemGet.getAvailable());
    }
}