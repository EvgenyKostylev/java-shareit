package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    private long id;

    private String name;

    @NotEmpty
    @Email
    private String email;
}
