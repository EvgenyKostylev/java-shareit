package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
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
public class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto user;
    private UserDto bookerUser;
    private ItemDto item;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeAll
    public void beforeAll() {
        UserDto userDto = UserDto.builder().name("name").email("email").build();

        user = userService.save(userDto);
        userDto.setName("booker name");
        userDto.setEmail("booker email");
        bookerUser = userService.save(userDto);
        item = ItemDto.builder().name("name").description("description").available(true).build();
        item = itemService.saveItem(item, user.getId());
    }

    @BeforeEach
    public void beforeEach() {
        bookingRepository.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testSaveBooking() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        BookingOutDto bookingSave = bookingService.save(bookingDto, bookerUser.getId());

        assertNotEquals(0, bookingSave.getId());
    }

    @Test
    public void testSaveBookingOverlappingTime() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        assertThrows(ValidationException.class, () -> bookingService.save(bookingDto, bookerUser.getId()));
    }

    @Test
    public void testUpdateBooking() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        BookingOutDto bookingSave = bookingService.save(bookingDto, bookerUser.getId());
        BookingOutDto bookingUpdate = bookingService.update(bookingSave.getId(), true, user.getId());

        assertEquals(bookingSave.getId(), bookingUpdate.getId());
        assertEquals(Status.APPROVED, bookingUpdate.getStatus());
    }

    @Test
    public void testUpdateBookingWithExistingBookingStatus() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        BookingOutDto bookingSave = bookingService.save(bookingDto, bookerUser.getId());
        bookingService.update(bookingSave.getId(), true, user.getId());

        assertThrows(ForbiddenException.class, () -> bookingService.update(
                bookingSave.getId(),
                false,
                user.getId()));
    }

    @Test
    public void testGetBookingById() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        BookingOutDto bookingSave = bookingService.save(bookingDto, bookerUser.getId());
        BookingOutDto bookingGet = bookingService.get(bookingSave.getId(), bookerUser.getId());

        assertEquals(bookingSave.getId(), bookingGet.getId());
    }

    @Test
    public void testGetBookingByIdNotRelatedToTheUser() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        BookingOutDto bookingSave = bookingService.save(bookingDto, bookerUser.getId());

        assertThrows(ValidationException.class, () -> bookingService.get(bookingSave.getId(), 1000));
    }

    @Test
    public void testGetAllBookingsByBookerWithStateAll() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        BookingOutDto bookingSave = bookingService.save(bookingDto, bookerUser.getId());
        List<BookingOutDto> bookingsGet = bookingService.getAllByUserId(State.ALL, bookerUser.getId());

        assertEquals(1, bookingsGet.size());
        assertEquals(bookingSave.getId(), bookingsGet.getFirst().getId());
        assertEquals(bookingSave.getBooker().getId(), bookingsGet.getFirst().getBooker().getId());
    }

    @Test
    public void testGetAllBookingsByBookerWithStateCurrent() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        bookingService.save(bookingDto, bookerUser.getId());
        List<BookingOutDto> bookingsGet = bookingService.getAllByUserId(State.CURRENT, bookerUser.getId());

        assertEquals(0, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByBookerWithStatePast() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        bookingService.save(bookingDto, bookerUser.getId());
        List<BookingOutDto> bookingsGet = bookingService.getAllByUserId(State.PAST, bookerUser.getId());

        assertEquals(0, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByBookerWithStateFuture() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        bookingService.save(bookingDto, bookerUser.getId());
        List<BookingOutDto> bookingsGet = bookingService.getAllByUserId(State.FUTURE, bookerUser.getId());

        assertEquals(0, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByBookerWithStateWaiting() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        bookingService.save(bookingDto, bookerUser.getId());
        List<BookingOutDto> bookingsGet = bookingService.getAllByUserId(State.WAITING, bookerUser.getId());

        assertEquals(1, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByBookerWithStateRejected() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        bookingService.save(bookingDto, bookerUser.getId());
        List<BookingOutDto> bookingsGet = bookingService.getAllByUserId(State.REJECTED, bookerUser.getId());

        assertEquals(0, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByOwnerWithStateAll() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();

        itemDto = itemService.saveItem(itemDto, user.getId());
        bookingDto.setItemId(itemDto.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        List<BookingOutDto> bookingsGet = bookingService.getAllByOwnerId(State.ALL, user.getId());

        assertEquals(2, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByOwnerWithStateCurrent() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();

        itemDto = itemService.saveItem(itemDto, user.getId());
        bookingDto.setItemId(itemDto.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        List<BookingOutDto> bookingsGet = bookingService.getAllByOwnerId(State.CURRENT, user.getId());

        assertEquals(0, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByOwnerWithStatePast() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();

        itemDto = itemService.saveItem(itemDto, user.getId());
        bookingDto.setItemId(itemDto.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        List<BookingOutDto> bookingsGet = bookingService.getAllByOwnerId(State.PAST, user.getId());

        assertEquals(0, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByOwnerWithStateFuture() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();

        itemDto = itemService.saveItem(itemDto, user.getId());
        bookingDto.setItemId(itemDto.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        List<BookingOutDto> bookingsGet = bookingService.getAllByOwnerId(State.FUTURE, user.getId());

        assertEquals(0, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByOwnerWithStateWaiting() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();

        itemDto = itemService.saveItem(itemDto, user.getId());
        bookingDto.setItemId(itemDto.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        List<BookingOutDto> bookingsGet = bookingService.getAllByOwnerId(State.WAITING, user.getId());

        assertEquals(2, bookingsGet.size());
    }

    @Test
    public void testGetAllBookingsByOwnerWithStateRejected() {
        BookingInDto bookingDto = new BookingInDto();

        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        ItemDto itemDto = ItemDto.builder().name("name").description("description").available(true).build();

        itemDto = itemService.saveItem(itemDto, user.getId());
        bookingDto.setItemId(itemDto.getId());
        bookingService.save(bookingDto, bookerUser.getId());

        List<BookingOutDto> bookingsGet = bookingService.getAllByOwnerId(State.REJECTED, user.getId());

        assertEquals(0, bookingsGet.size());
    }
}