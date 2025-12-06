package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();

    public UserDto save(UserDto user) {
        if (isEmailUsed(user.getEmail())) {
            throw new ValidationException(String.format("Почта %s уже используется", user.getEmail()));
        }

        user.setId((long) user.hashCode());
        users.put(user.getId(), UserMapper.toUser(user));

        if (!users.containsKey(user.getId())) {
            throw new ValidationException(String.format("Не удалось добавить пользователя: " + user));
        }

        return UserMapper.toUserDto(users.get(user.getId()));
    }

    public UserDto update(UserDto user, long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }

        if (isEmailUsed(user.getEmail(), userId)) {
            throw new ValidationException(String.format("Почта %s уже используется", user.getEmail()));
        }

        users.put(userId, UserMapper.updateUserFields(users.get(userId), user));

        return UserMapper.toUserDto(users.get(userId));
    }

    public UserDto get(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }

        return UserMapper.toUserDto(users.get(userId));
    }

    public List<UserDto> getAll() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public void delete(long userId) {
        users.remove(userId);
    }

    private boolean isEmailUsed(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isEmailUsed(String email, long userId) {
        Optional<User> userOptional = users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();

        return userOptional.isPresent() && userOptional.get().getId() != userId;
    }
}