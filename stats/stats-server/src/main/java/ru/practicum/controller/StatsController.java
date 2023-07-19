package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.security.InvalidParameterException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {
    private final StatsService server;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void saveStats(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("Сохранена информация по hit: {}", endpointHitDto);
        server.saveStats(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@NotEmpty @RequestParam(value = "start") String start,
                                       @NotEmpty @RequestParam(value = "end") String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique)  {
        log.info("Get запрос на получение статистики по посещениям");
        LocalDateTime startDate = parseDate(start);
        LocalDateTime endDate = parseDate(end);
        if (endDate.isBefore(startDate)) {
            log.info("Неккоректный формат дат start time {} и end time {}", start, end);
            throw new InvalidParameterException("Неккоректный формат дат");
        }
        return server.getStats(startDate, endDate, uris, unique);
    }

    private LocalDateTime parseDate(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeException exception) {
            throw new InvalidParameterException("Неккоректный формат даты: " + date);
        }
    }
}
