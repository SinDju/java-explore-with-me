package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDto {
    private Long id;
    private UserShortDto user;
    private EventShortDto event;
    private Long rating;
}
