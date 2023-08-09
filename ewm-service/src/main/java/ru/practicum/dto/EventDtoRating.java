package ru.practicum.dto;

import lombok.*;
import ru.practicum.model.Event;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDtoRating {
    private Event event;
    private Long rating;
}