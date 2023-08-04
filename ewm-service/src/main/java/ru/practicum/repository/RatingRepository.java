package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.EventDtoRating;
import ru.practicum.model.Event;
import ru.practicum.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Boolean existsByEventId(Long eventId);

    Optional<Rating> findByEventId(Long eventId);

    List<Rating>  findAllByEventIdIn(List<Event> events);
}
