package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        User user = new User();

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public static UserDto updateUserFields(User user, UserDto updatedUser) {
        if (updatedUser.hasName()) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.hasEmail()) {
            user.setEmail(updatedUser.getEmail());
        }

        return toUserDto(user);
    }
}