package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto save(UserDto user) {
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
    }

    @Override
    @Transactional
    public UserDto update(UserDto user, long userId) {
        user.setId(userId);

        return UserMapper.updateUserFields(getUserById(userId), user);
    }

    @Override
    public UserDto get(long userId) {
        return UserMapper.toUserDto(getUserById(userId));
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public void delete(long userId) {
        repository.deleteById(userId);
    }

    @Override
    public User getUserById(long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }
}