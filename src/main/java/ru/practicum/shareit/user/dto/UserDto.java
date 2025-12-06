package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotBlank
    private String name;

    @NotEmpty
    @Email
    private String email;

    public boolean hasName() {
        return name != null && !name.isEmpty();
    }

    public boolean hasEmail() {
        return email != null && !email.isEmpty();
    }
}