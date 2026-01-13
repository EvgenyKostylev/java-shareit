package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ActiveProfiles("test")
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mock;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testItemRequestSave() throws Exception {
        ItemRequestDto request = ItemRequestDto.builder()
                .description("description")
                .build();

        ItemRequestDto response = ItemRequestDto.builder()
                .id(1L)
                .requestor(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.save(any(ItemRequestDto.class), anyLong()))
                .thenReturn(response);

        mock.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requestor").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    public void testItemRequestsGetByUserId() throws Exception {
        List<ItemRequestDto> response = List.of(ItemRequestDto.builder()
                        .id(1L)
                        .requestor(1L)
                        .description("first description")
                        .created(LocalDateTime.now())
                        .build(),
                ItemRequestDto.builder()
                        .id(2L)
                        .requestor(1L)
                        .description("second description")
                        .created(LocalDateTime.now())
                        .build());

        when(itemRequestService.getUserRequests(1L))
                .thenReturn(response);

        mock.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].requestor").value(1L))
                .andExpect(jsonPath("$[1].requestor").value(1L));
    }

    @Test
    public void testItemRequestsGetAll() throws Exception {
        List<ItemRequestDto> response = List.of(ItemRequestDto.builder()
                        .id(1L)
                        .requestor(1L)
                        .description("first description")
                        .created(LocalDateTime.now())
                        .build(),
                ItemRequestDto.builder()
                        .id(2L)
                        .requestor(1L)
                        .description("second description")
                        .created(LocalDateTime.now())
                        .build());

        when(itemRequestService.getAll())
                .thenReturn(response);

        mock.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].description").value("first description"))
                .andExpect(jsonPath("$[1].description").value("second description"));
    }

    @Test
    public void testItemRequestGetById() throws Exception {
        ItemRequestDto response = ItemRequestDto.builder()
                .id(1L)
                .requestor(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.get(1L))
                .thenReturn(response);

        mock.perform(get("/requests/{requestId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requestor").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }
}