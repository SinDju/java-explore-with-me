package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDtoEvent {
    private EventShortDto event;
    private Long rating;
}
