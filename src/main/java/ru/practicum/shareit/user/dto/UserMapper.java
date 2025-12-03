package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

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
                .id(userDto.id())
                .name(userDto.name())
                .email(userDto.email())
                .build();
    }

    public static User updateUserFields(User user, UpdatedUser updatedUser) {
        if (updatedUser.hasName()) {
            user.setName(updatedUser.name());
        }
        if (updatedUser.hasEmail()) {
            user.setEmail(updatedUser.email());
        }

        return user;
    }
}