package ru.practicum.shareit.user.service;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase
@Transactional
public class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        userRepository.deleteAll();
    }

    @Test
    public void testSaveUser() {
        UserDto userDto = UserDto.builder().name("name").email("email").build();
        UserDto userSave = userService.save(userDto);

        assertNotNull(userSave.getId());
        assertEquals("name", userSave.getName());
        assertEquals("email", userSave.getEmail());
    }

    @Test
    public void testUpdateUser() {
        UserDto userDto = UserDto.builder().name("name").email("email").build();
        UserDto userSave = userService.save(userDto);

        userSave.setName("updated name");

        UserDto userUpdate = userService.update(userSave, userSave.getId());

        assertEquals(userSave.getId(), userUpdate.getId());
        assertEquals(userSave.getName(), userUpdate.getName());
    }

    @Test
    public void testGetUserById() {
        UserDto userDto = UserDto.builder().name("name").email("email").build();
        UserDto userSave = userService.save(userDto);

        UserDto userGet = userService.get(userSave.getId());

        assertEquals(userSave, userGet);
    }

    @Test
    public void testGetAllUsers() {
        UserDto userDto = UserDto.builder().name("first name").email("email").build();

        userService.save(userDto);
        userDto.setName("second name");
        userDto.setEmail("second email");
        userService.save(userDto);

        List<UserDto> usersDto = userService.getAll();

        assertEquals(2, usersDto.size());
    }

    @Test
    public void testDeleteUser() {
        UserDto userDto = UserDto.builder().name("name").email("email").build();
        UserDto userSave = userService.save(userDto);

        userService.delete(userSave.getId());

        assertThrows(NotFoundException.class, () -> userService.get(userSave.getId()));
    }
}