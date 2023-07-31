package ru.practicum.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserRequest {
    @NotBlank
    @Length(max = 250, min = 2)
    private String name;
    @Email
    @NotBlank
    @Length(max = 254, min = 6)
    private String email;
}
