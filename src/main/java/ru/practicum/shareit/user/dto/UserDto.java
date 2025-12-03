package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

public record UserDto(long id, String name, @Email String email) {
}