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
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User updateUserFields(User user, UserDto updatedUser) {
        if (updatedUser.hasName()) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.hasEmail()) {
            user.setEmail(updatedUser.getEmail());
        }

        return user;
    }
}