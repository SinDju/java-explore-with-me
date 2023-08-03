package ru.practicum.service;

import ru.practicum.dto.RatingDto;
import ru.practicum.dto.RatingDtoEvent;
import ru.practicum.dto.RatingDtoUser;

import java.util.List;

public interface RatingService {

    RatingDtoEvent getRatingByEventId(Long eventId);

    RatingDtoUser getRatingByUserId(Long userId);

    List<RatingDtoEvent> getAllRatingsEvent();

    RatingDto addDislike(Long eventId, Long userId);

    RatingDto addLike(Long eventId, Long userId);

    void deleteLikeOrDislike(Long ratingId, Long userId);
}
