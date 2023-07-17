package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query
            ("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(eh.ip)) " +
                    "FROM EndpointHit AS eh " +
                    "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
                    "AND (eh.uri in ?3) " +
                    "GROUP BY eh.app, eh.uri " +
                    "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> getAllByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query
            ("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(eh.ip)) " +
                    "FROM EndpointHit AS eh " +
                    "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
                    "GROUP BY eh.app, eh.uri " +
                    "ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> getAllNotUris(LocalDateTime start, LocalDateTime end);

    @Query
            ("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(distinct eh.ip)) " +
                    "FROM EndpointHit AS eh " +
                    "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
                    "AND (eh.uri in ?3) " +
                    "GROUP BY eh.app, eh.uri " +
                    "ORDER BY COUNT(distinct eh.ip) DESC")
    List<ViewStats> getAllByUrisUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query
            ("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(distinct eh.ip)) " +
                    "FROM EndpointHit AS eh " +
                    "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
                    "GROUP BY eh.app, eh.uri " +
                    "ORDER BY COUNT(distinct eh.ip) DESC")
    List<ViewStats> getAllByUniqueIpNotUris(LocalDateTime start, LocalDateTime end);
}
