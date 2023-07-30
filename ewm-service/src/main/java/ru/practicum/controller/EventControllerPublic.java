package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/events")
public class EventControllerPublic {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEvents(@RequestParam(value = "text", defaultValue = "") String text,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(defaultValue = "false") Boolean paid,
                                            @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
                                            HttpServletRequest request) {
        log.info("GET запрос на получения событий с возможностью фильтрации");
        return eventService.getAllEventFromPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable(value = "eventId") @Min(1) Long eventId,
                                     HttpServletRequest request) {
        log.info("GET запрос на получения подробной информации о событии");
        return eventService.getEventById(eventId, request);
    }
}
