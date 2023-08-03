package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.EventDtoRating;
import ru.practicum.model.Rating;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query(value = "select sum(r.rating) from ratings as r " +
                    "where r.event_id = ?1 ", nativeQuery = true)
    Long findByEventAndRatingSum(Long eventId);

    @Query(value = "select new ru.practicum.dto.EventDtoRating(ev.*, sum(r.rating)) " +
            "from ratings as r " +
            "JOIN events as ev " +
            "ON r.event_id = ev.event_id " +
            "order by sum(r.rating) DESC ", nativeQuery = true)
    List<EventDtoRating> findAllEventAndRatingSum();
}
