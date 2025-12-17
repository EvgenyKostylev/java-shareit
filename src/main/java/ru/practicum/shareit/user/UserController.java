package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserDto save(@Valid @RequestBody UserDto request) {
        return userService.save(request);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto request, @PathVariable("userId") long userId) {
        return userService.update(request, userId);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable("userId") long userId) {
        return userService.get(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") long userId) {
        userService.delete(userId);
    }
}