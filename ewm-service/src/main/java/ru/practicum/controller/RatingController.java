package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.RatingDto;
import ru.practicum.dto.RatingDtoEvent;
import ru.practicum.dto.RatingDtoUser;
import ru.practicum.service.RatingService;

import javax.validation.constraints.Positive;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping("/{eventId}/like/{userId}")
    public RatingDto addLike(@PathVariable @Positive Long eventId, @PathVariable @Positive Long userId) {
        log.info("POST запрос на добавление лайка от пользователя с event_id {} в отзыв с event_id: {}", eventId, userId);
        return ratingService.addLike(eventId, userId);
    }

    @PostMapping("/{eventId}/dislike/{userId}")
    public RatingDto addDislike(@PathVariable @Positive Long eventId, @PathVariable @Positive Long userId) {
        log.info("POST запрос на добавление дизлайка от пользователя с id {} в отзыв с id: {}", eventId, userId);
        return ratingService.addDislike(eventId, userId);
    }

    @DeleteMapping("/{likesOrDislikesId}/users/{userId}")
    public void deleteLikeOrDislike(@PathVariable @Positive Long likesOrDislikesId, @PathVariable @Positive Long userId) {
        log.info("DELETE запрос на удаление лайка или дизлайка с id {} от пользователя с id {}", likesOrDislikesId, userId);
        ratingService.deleteLikeOrDislike(likesOrDislikesId, userId);
    }

    @GetMapping("/{eventId}")
    public RatingDtoEvent getRatingByEventId(@PathVariable @Positive Long eventId) {
        log.info("GET запрос на получение рейтинга события с id: {}", eventId);
        return ratingService.getRatingByEventId(eventId);
    }

    @GetMapping
    public List<RatingDtoEvent> getAllRatingEvent() {
        log.info("GET запрос на получение рейтинга событий");
        return ratingService.getAllRatingsEvent();
    }

    @GetMapping("/users/{userId}")
    public RatingDtoUser getRatingByUserId(@PathVariable @Positive Long userId) {
        log.info("GET запрос на получение рейтинга автора с id: {}", userId);
        return ratingService.getRatingByUserId(userId);
    }
}
