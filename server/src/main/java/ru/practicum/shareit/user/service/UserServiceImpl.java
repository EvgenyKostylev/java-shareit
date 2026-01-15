package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto save(UserDto request) {
        UserDto userDto = UserMapper.toUserDto(repository.save(UserMapper.toUser(request)));

        log.info("Пользователь успешно создан: userId={}", userDto.getId());

        return userDto;
    }

    @Override
    @Transactional
    public UserDto update(UserDto user, long userId) {
        user.setId(userId);

        UserDto userDto = UserMapper.updateUserFields(getUserById(userId), user);

        log.info("Пользователь изменен userId={}", userDto.getId());

        return userDto;
    }

    @Override
    public UserDto get(long userId) {
        UserDto userDto = UserMapper.toUserDto(getUserById(userId));

        log.info("Вывод пользователя userId={}", userDto.getId());

        return userDto;
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> usersDto = repository.findAll().stream().map(UserMapper::toUserDto).toList();

        log.info("Вывод {} пользователей", usersDto.size());

        return usersDto;
    }

    @Override
    public void delete(long userId) {
        log.info("Удаление пользователя userId={}", userId);

        repository.deleteById(userId);
    }

    @Override
    public User getUserById(long userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));

        log.info("Найден пользователь userId={}", userId);

        return user;
    }
}