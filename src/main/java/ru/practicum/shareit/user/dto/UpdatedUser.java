package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

public record UpdatedUser(long id, String name, @Email String email) {
    public boolean hasName() {
        return name != null && !name.isEmpty();
    }

    public boolean hasEmail() {
        return email != null && !email.isEmpty();
    }
}