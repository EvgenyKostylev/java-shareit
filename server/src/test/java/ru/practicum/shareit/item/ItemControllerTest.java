package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@ActiveProfiles("test")
public class ItemControllerTest {
    @Autowired
    private MockMvc mock;

    @MockBean
    private ItemServiceImpl itemService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testItemSave() throws Exception {
        ItemDto request = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemDto response = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .ownerId(1L)
                .build();

        when(itemService.saveItem(any(ItemDto.class), anyLong()))
                .thenReturn(response);

        mock.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.ownerId").value(1L));
    }

    @Test
    public void testCommentSave() throws Exception {
        CommentDto request = CommentDto.builder()
                .text("text")
                .build();

        CommentDto response = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .build();

        when(itemService.saveComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(response);

        mock.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("text"))
                .andExpect(jsonPath("$.authorName").value("author"));
    }

    @Test
    public void testItemUpdate() throws Exception {
        ItemDto request = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemDto response = ItemDto.builder()
                .id(1L)
                .name("update name")
                .description("description")
                .available(true)
                .ownerId(1L)
                .build();

        when(itemService.update(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(response);

        mock.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("update name"));
    }

    @Test
    public void testItemGet() throws Exception {
        ItemBookingDto response = ItemBookingDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .ownerId(1L)
                .build();

        when(itemService.get(1L, 1L))
                .thenReturn(response);

        mock.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.ownerId").value(1L));
    }

    @Test
    public void testItemsGetByUserId() throws Exception {
        List<ItemBookingDto> response = List.of(ItemBookingDto.builder()
                        .id(1L)
                        .name("first name")
                        .description("first description")
                        .available(true)
                        .ownerId(1L)
                        .build(),
                ItemBookingDto.builder()
                        .id(2L)
                        .name("second name")
                        .description("second description")
                        .available(true)
                        .ownerId(1L)
                        .build());

        when(itemService.getAllByUserId(1L))
                .thenReturn(response);

        mock.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("first name"))
                .andExpect(jsonPath("$[1].name").value("second name"));
    }

    @Test
    public void testItemsGetByName() throws Exception {
        List<ItemDto> response = List.of(ItemDto.builder()
                        .id(1L)
                        .name("first name")
                        .description("first description")
                        .available(true)
                        .ownerId(1L)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("second name")
                        .description("second description")
                        .available(true)
                        .ownerId(1L)
                        .build());

        when(itemService.findByName("name"))
                .thenReturn(response);

        mock.perform(get("/items/search")
                        .param("text", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("first name"))
                .andExpect(jsonPath("$[1].name").value("second name"));
    }
}