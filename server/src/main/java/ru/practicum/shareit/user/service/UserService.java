package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto save(UserDto user);

    UserDto update(UserDto user, long userId);

    UserDto get(long userId);

    List<UserDto> getAll();

    void delete(long userId);

    User getUserById(long userId);
}