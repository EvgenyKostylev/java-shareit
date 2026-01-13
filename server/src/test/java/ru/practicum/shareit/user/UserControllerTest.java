package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mock;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testUserSave() throws Exception {
        UserDto request = UserDto.builder()
                .name("name")
                .email("email")
                .build();

        UserDto response = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email")
                .build();

        when(userService.save(any(UserDto.class)))
                .thenReturn(response);

        mock.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email"));
    }

    @Test
    public void testUserSaveWithExistingEmail() throws Exception {
        UserDto request = UserDto.builder()
                .name("name")
                .email("email")
                .build();

        when(userService.save(any(UserDto.class)))
                .thenThrow(new DataIntegrityViolationException("dataIntegrityViolation error"));

        mock.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Ошибка валидации"))
                .andExpect(jsonPath("$.description").value("dataIntegrityViolation error"));
    }

    @Test
    public void testUserUpdate() throws Exception {
        UserDto request = UserDto.builder()
                .name("update name")
                .email("email")
                .build();

        UserDto response = UserDto.builder()
                .id(1L)
                .name("update name")
                .email("email")
                .build();

        when(userService.update(any(UserDto.class), eq(1L)))
                .thenReturn(response);

        mock.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("update name"));
    }

    @Test
    public void testUserGet() throws Exception {
        UserDto response = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email")
                .build();

        when(userService.get(1L))
                .thenReturn(response);

        mock.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email"));
    }

    @Test
    public void testUserGetAll() throws Exception {
        List<UserDto> response = List.of(UserDto.builder()
                        .id(1L)
                        .name("first name")
                        .email("first email")
                        .build(),
                UserDto.builder()
                        .id(2L)
                        .name("second name")
                        .email("second email")
                        .build());

        when(userService.getAll())
                .thenReturn(response);

        mock.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("first name"))
                .andExpect(jsonPath("$[1].name").value("second name"));
    }

    @Test
    public void testUserDelete() throws Exception {
        doNothing().when(userService).delete(1L);

        mock.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }
}