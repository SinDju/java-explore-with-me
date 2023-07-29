package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class NewUserRequest {
    @NotBlank
    @Size(max = 250, min = 2)
    private String name;
    @Email
    @NotBlank
    @Size(max = 254, min = 6)
    private String email;
}
