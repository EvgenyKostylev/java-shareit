package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UpdatedUser;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(User user);

    UserDto update(UpdatedUser user, long userId);

    UserDto get(long userId);

    List<UserDto> getAll();

    void delete(long userId);
}