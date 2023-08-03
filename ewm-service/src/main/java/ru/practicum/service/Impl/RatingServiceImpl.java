package ru.practicum.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EventDtoRating;
import ru.practicum.dto.RatingDto;
import ru.practicum.dto.RatingDtoEvent;
import ru.practicum.dto.RatingDtoUser;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.RatingMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Rating;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RatingRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.RatingService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Override
    public RatingDtoEvent getRatingByEventId(Long eventId) {
        Event event = checkEvent(eventId);
        Long rating = ratingRepository.findByEventAndRatingSum(eventId);
        return RatingMapper.toRatingDtoEvent(rating, event);
    }

    @Override
    public RatingDtoUser getRatingByUserId(Long userId) {
        Long rating = 0L;
        User user = checkUser(userId);
        List<Long> eventsId = eventRepository.findByInitiatorId(userId)
                .stream().map(Event::getId).collect(Collectors.toList());
        for (Long eventId : eventsId) {
            rating = rating + ratingRepository.findByEventAndRatingSum(eventId);
        }
        return RatingMapper.toRatingDtoUser(rating, user);
    }

    @Override
    public List<RatingDtoEvent> getAllRatingsEvent() {
        List<EventDtoRating> eventDtoRatings = ratingRepository.findAllEventAndRatingSum();
        return eventDtoRatings.stream().map(RatingMapper::toRatingDtoEvent).collect(Collectors.toList());
    }

    @Override
    public RatingDto addDislike(Long eventId, Long userId) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        Rating rating = RatingMapper.toRating(event, user, -1L);
        return RatingMapper.toRatingDto(ratingRepository.save(rating), event, user);
    }

    @Override
    public RatingDto addLike(Long eventId, Long userId) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        Rating rating = RatingMapper.toRating(event, user, 1L);
        return RatingMapper.toRatingDto(ratingRepository.save(rating), event, user);
    }

    @Override
    public void deleteLikeOrDislike(Long ratingId, Long userId) {
        checkUser(userId);
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() ->
                new ObjectNotFoundException("Рейтинга с ID " + ratingId + " не найждено"));
        if (!rating.getUser().getId().equals(userId)) {
            throw new ConflictException("Пользователь с Id " + userId + " не имеет доступа к данному рейтингу");
        }
        ratingRepository.deleteById(ratingId);
    }


    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("События с ID " + eventId + " не существует"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь с ID " + userId + " не найден или недоступен"));
    }
}