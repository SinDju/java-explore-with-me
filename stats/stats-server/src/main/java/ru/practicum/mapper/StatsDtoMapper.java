package ru.practicum.mapper;


import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StatsDtoMapper {
    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }

    public static EndpointHit toStats(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                endpointHitDto.getId(),
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp()
        );
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto(
                viewStats.getApp(),
                viewStats.getUri(),
                viewStats.getHits());
    }

    public static List<ViewStatsDto> toDtoViewStatsDtoList(Iterable<ViewStats> viewStats) {
        List<ViewStatsDto> result = new ArrayList<>();
        Long i = 0L;
        for (ViewStats viewStats1 : viewStats) {
            i++;
            result.add(toViewStatsDto(viewStats1));
        }

        return result;
    }

   /* public static List<ViewStatsDto> viewToDto(Iterable<ViewStats> viewStats) {
        List<ViewStatsDto> result = new ArrayList<>();

        for (ViewStats element : viewStats) {
            result.add(toDto(element));
        }

        return result;
    }*/
}
