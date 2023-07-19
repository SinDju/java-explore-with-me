package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.StatsDtoMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public void saveStats(EndpointHitDto endpointHitDto) {
        EndpointHit object = StatsDtoMapper.toStats(endpointHitDto);
        EndpointHit endpointHit = repository.save(object);
        log.info("Сохранен объект {} в репозиторий", endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime startDate, LocalDateTime endDate, List<String> uris, Boolean unique) {
        if (uris != null && uris.size() > 0) {
            return unique ? StatsDtoMapper.toDtoViewStatsDtoList(repository.getAllByUrisUniqueIp(startDate, endDate,
                    uris)) : StatsDtoMapper.toDtoViewStatsDtoList(repository.getAllByUris(startDate, endDate, uris));
        }

        return unique ? StatsDtoMapper.toDtoViewStatsDtoList(repository.getAllByUniqueIpNotUris(startDate, endDate)) :
                StatsDtoMapper.toDtoViewStatsDtoList(repository.getAllNotUris(startDate, endDate));
    }
}
