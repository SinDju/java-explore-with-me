package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {
    private final StatsService server;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void saveStats(EndpointHitDto endpointHitDto) {
        log.info("Сохранена информация по hit: {}", endpointHitDto);
        server.saveStats(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@NotEmpty @RequestParam(value = "start") String start,
                                       @NotEmpty @RequestParam(value = "end") String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique)  {
        log.info("Get запрос на получение статистики по посещениям");
        return server.getStats(start, end, uris, unique);
    }
}
