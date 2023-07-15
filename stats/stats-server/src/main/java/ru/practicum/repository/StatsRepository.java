package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query
            ("select s.app, s.uri, count(s.ip) from EndpointHit s " +
                    "where s.timestamp BETWEEN ?1 and ?2 " +
                    "and (s.uri in ?3) " +
                    "GROUP BY s.app, s.uri " +
                    "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getAllByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query
            ("select s.app, s.uri, count(DISTINCT s.ip) from EndpointHit s " +
                    "where s.timestamp BETWEEN ?1 and ?2 " +
                    "and (s.uri in ?3) " +
                    "GROUP BY s.app, s.uri " +
                    "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getAllByUrisUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
