package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ActiveProfiles("test")
public class BookingControllerTest {
    @Autowired
    private MockMvc mock;

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private ObjectMapper mapper;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    public void testBookingSave() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingInDto request = new BookingInDto();

        request.setStart(now);
        request.setEnd(now);
        request.setItemId(1L);

        BookingOutDto response = new BookingOutDto();

        response.setId(1L);
        response.setStart(now);
        response.setEnd(now);
        response.setItem(ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .available(true)
                .build());
        response.setBooker(UserDto.builder().id(1L).name("name").email("email").build());
        response.setStatus(Status.WAITING);

        when(bookingService.save(any(BookingInDto.class), anyLong()))
                .thenReturn(response);

        mock.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value(now.format(formatter)))
                .andExpect(jsonPath("$.end").value(now.format(formatter)))
                .andExpect(jsonPath("$.status").value(Status.WAITING.toString()));
    }

    @Test
    public void testBookingSaveByItemOwner() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingInDto request = new BookingInDto();

        request.setStart(now);
        request.setEnd(now);
        request.setItemId(1L);

        when(bookingService.save(any(BookingInDto.class), eq(1L)))
                .thenThrow(new ValidationException("validation error"));

        mock.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ошибка валидации"))
                .andExpect(jsonPath("$.description").value("validation error"));
    }

    @Test
    public void testBookingUpdate() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingOutDto response = new BookingOutDto();

        response.setId(1L);
        response.setStart(now);
        response.setEnd(now);
        response.setItem(ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .available(true)
                .build());
        response.setBooker(UserDto.builder().id(1L).name("name").email("email").build());
        response.setStatus(Status.APPROVED);

        when(bookingService.update(1L, true, 1))
                .thenReturn(response);

        mock.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value(now.format(formatter)))
                .andExpect(jsonPath("$.end").value(now.format(formatter)))
                .andExpect(jsonPath("$.status").value(Status.APPROVED.toString()));
    }

    @Test
    public void testBookingUpdateByBooker() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingOutDto response = new BookingOutDto();

        response.setId(1L);
        response.setStart(now);
        response.setEnd(now);
        response.setItem(ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .available(true)
                .build());
        response.setBooker(UserDto.builder().id(1L).name("name").email("email").build());
        response.setStatus(Status.APPROVED);

        when(bookingService.update(1L, true, 2L))
                .thenThrow(new ForbiddenException("forbidden error"));

        mock.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Ошибка доступа к данным"))
                .andExpect(jsonPath("$.description").value("forbidden error"));
    }

    @Test
    public void testBookingGet() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingOutDto response = new BookingOutDto();

        response.setId(1L);
        response.setStart(now);
        response.setEnd(now);
        response.setItem(ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .available(true)
                .build());
        response.setBooker(UserDto.builder().id(1L).name("name").email("email").build());
        response.setStatus(Status.WAITING);

        when(bookingService.get(1L, 1L))
                .thenReturn(response);

        mock.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.start").value(now.format(formatter)))
                .andExpect(jsonPath("$.end").value(now.format(formatter)))
                .andExpect(jsonPath("$.status").value(Status.WAITING.toString()));
    }

    @Test
    public void testBookingsGetByBooker() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingOutDto firstResponse = new BookingOutDto();

        firstResponse.setId(1L);
        firstResponse.setStart(now);
        firstResponse.setEnd(now);
        firstResponse.setItem(ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .available(true)
                .build());
        firstResponse.setBooker(UserDto.builder().id(1L).name("name").email("email").build());
        firstResponse.setStatus(Status.WAITING);

        BookingOutDto secondResponse = new BookingOutDto();

        secondResponse.setId(2L);
        secondResponse.setStart(now);
        secondResponse.setEnd(now);
        secondResponse.setItem(ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .available(true)
                .build());
        secondResponse.setBooker(UserDto.builder().id(1L).name("name").email("email").build());
        secondResponse.setStatus(Status.WAITING);

        List<BookingOutDto> response = List.of(firstResponse, secondResponse);

        when(bookingService.getAllByUserId(State.ALL, 1L))
                .thenReturn(response);

        mock.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void testBookingsGetByOwner() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingOutDto firstResponse = new BookingOutDto();

        firstResponse.setId(1L);
        firstResponse.setStart(now);
        firstResponse.setEnd(now);
        firstResponse.setItem(ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .available(true)
                .build());
        firstResponse.setBooker(UserDto.builder().id(1L).name("name").email("email").build());
        firstResponse.setStatus(Status.WAITING);

        BookingOutDto secondResponse = new BookingOutDto();

        secondResponse.setId(2L);
        secondResponse.setStart(now);
        secondResponse.setEnd(now);
        secondResponse.setItem(ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .ownerId(1L)
                .available(true)
                .build());
        secondResponse.setBooker(UserDto.builder().id(1L).name("name").email("email").build());
        secondResponse.setStatus(Status.WAITING);

        List<BookingOutDto> response = List.of(firstResponse, secondResponse);

        when(bookingService.getAllByOwnerId(State.ALL, 1L))
                .thenReturn(response);

        mock.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    public void testBookingsGetByOwnerWithoutItems() throws Exception {
        when(bookingService.getAllByOwnerId(State.ALL, 1L))
                .thenThrow(new NotFoundException("not found error"));

        mock.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ошибка данных"))
                .andExpect(jsonPath("$.description").value("not found error"));
    }
}