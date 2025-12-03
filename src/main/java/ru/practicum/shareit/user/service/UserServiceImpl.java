package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UpdatedUser;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();

    public UserDto save(User user) {
        if (isEmailUsed(user.getEmail())) {
            throw new ValidationException(String.format("Почта %s уже используется", user.getEmail()));
        }

        user.setId(user.hashCode());
        users.put(user.getId(), user);

        if (users.containsKey(user.getId())) {
            return UserMapper.toUserDto(users.get(user.getId()));
        }
        throw new ValidationException(String.format("Не удалось добавить пользователя: " + user));
    }

    public UserDto update(UpdatedUser user, long userId) {
        if (users.containsKey(userId)) {
            if (isEmailUsed(user.email(), userId)) {
                throw new ValidationException(String.format("Почта %s уже используется", user.email()));
            }

            users.put(userId, UserMapper.updateUserFields(users.get(userId), user));

            return UserMapper.toUserDto(users.get(userId));
        }
        throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
    }

    public UserDto get(long userId) {
        if (users.containsKey(userId)) {
            return UserMapper.toUserDto(users.get(userId));
        }
        throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
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