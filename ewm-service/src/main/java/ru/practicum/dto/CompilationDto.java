package ru.practicum.dto;

import ru.practicum.model.Event;

import java.util.List;

public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private boolean pinned;
    private String title;
}
