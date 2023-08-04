package ru.practicum.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventDtoRating;
import ru.practicum.dto.RatingDto;
import ru.practicum.dto.RatingDtoEvent;
import ru.practicum.dto.RatingDtoUser;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.RatingMapper;
import ru.practicum.mapper.RatingsLikesOrDislikesMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Rating;
import ru.practicum.model.RatingsLikesOrDislikes;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RatingRepository;
import ru.practicum.repository.RatingsLikesOrDislikesRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.RatingService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RatingsLikesOrDislikesRepository likesOrDislikesRepository;


    @Override
    public RatingDtoEvent getRatingByEventId(Long eventId) {
        Rating rating = ratingRepository.findByEventId(eventId).orElseThrow(() ->
                new ObjectNotFoundException("События с ID " + eventId + " не существует"));
        return RatingMapper.toRatingDtoEvent(rating);
    }

    @Override
    public RatingDtoUser getRatingByUserId(Long userId) {
        User user = checkUser(userId);
        List<Event> events = eventRepository.findByInitiatorId(userId);

        List<Rating> ratingsEvent = ratingRepository.findAllByEventIdIn(events);
        Long rating = ratingsEvent.stream().mapToLong(Rating::getRating).sum();
        return RatingMapper.toRatingDtoUser(rating, user);
    }

    @Override
    public List<RatingDtoEvent> getAllRatingsEvent() {
        List<Rating> ratingsEvents = ratingRepository.findAll();
        return ratingsEvents.stream().map(RatingMapper::toRatingDtoEvent).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RatingDto addDislike(Long eventId, Long userId) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        Boolean dislike = false;
        Long rating = 0L;

        if (ratingRepository.existsByEventId(eventId)) {
            rating = ratingRepository.findById(eventId).get().getRating();
        }
        RatingsLikesOrDislikes addDislike = likesOrDislikesRepository.save(RatingsLikesOrDislikesMapper
                .toRatingsLikesOrDislikes(user, event, dislike));
        Rating ratingEvent = RatingMapper.toRating(addDislike, --rating);
        return RatingMapper.toRatingDto(ratingRepository.save(ratingEvent), event, user);
    }

    @Transactional
    @Override
    public RatingDto addLike(Long eventId, Long userId) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        Boolean like = true;
        Long rating = 0L;

        if (ratingRepository.existsByEventId(eventId)) {
            rating = ratingRepository.findById(eventId).get().getRating();
        }
        RatingsLikesOrDislikes addLikes = likesOrDislikesRepository.save(RatingsLikesOrDislikesMapper
                .toRatingsLikesOrDislikes(user, event, like));
        Rating ratingEvent = RatingMapper.toRating(addLikes, ++rating);
        return RatingMapper.toRatingDto(ratingRepository.save(ratingEvent), event, user);
    }

    @Transactional
    @Override
    // в случае удаления лайка дизлайка необходимо уменьшить рейтинг в таблице рейтинга
    public void deleteLikeOrDislike(Long likesOrDislikesId, Long userId) {
        checkUser(userId);
        RatingsLikesOrDislikes likesOrDislikes = likesOrDislikesRepository.findById(likesOrDislikesId).orElseThrow(() ->
                new ObjectNotFoundException("Рейтинга с ID " + likesOrDislikesId + " не найдено"));
        if (!likesOrDislikes.getUser().getId().equals(userId)) {
            throw new ConflictException("Пользователь с Id " + userId + " не имеет доступа к данной оценке");
        }
        Rating ratings = ratingRepository.findByEventId(likesOrDislikes.getEvent().getId()).get();
        Long rating = ratings.getRating();
        if (likesOrDislikes.getIsPositive()) {
            --rating;
        } else {
            rating++;
        }
        ratings.setRating(rating);
        likesOrDislikesRepository.deleteById(likesOrDislikesId);
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