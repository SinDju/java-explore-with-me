package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.StatsDtoMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.security.InvalidParameterException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final StatsRepository repository;

    @Override
    public void saveStats(EndpointHitDto endpointHitDto) {
        EndpointHit object = StatsDtoMapper.toStats(endpointHitDto);
        EndpointHit endpointHit = repository.save(object);
        log.info("Сохранен объект {} в репозиторий", endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDate = parseDate(start);
        LocalDateTime endDate = parseDate(end);
        if (endDate.isBefore(startDate)) {
            log.info("Неккоректный формат дат start time {} и end time {}", start, end);
            throw new InvalidParameterException("Неккоректный формат дат");
        }
        if (uris != null && uris.size() > 0) {
            return unique ? StatsDtoMapper.toDtoViewStatsDtoList(repository.getAllByUrisUniqueIp(startDate, endDate,
                    uris)) : StatsDtoMapper.toDtoViewStatsDtoList(repository.getAllByUris(startDate, endDate, uris));
        }

        return unique ? StatsDtoMapper.toDtoViewStatsDtoList(repository.getAllByUniqueIpNotUris(startDate, endDate)) :
                StatsDtoMapper.toDtoViewStatsDtoList(repository.getAllNotUris(startDate, endDate));
    }

    private LocalDateTime parseDate(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeException exception) {
            throw new InvalidParameterException("Неккоректный формат даты: " + date);
        }
    }
}
