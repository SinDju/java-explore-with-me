package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.*;
import ru.practicum.model.Event;
import ru.practicum.model.Rating;
import ru.practicum.model.User;

@UtilityClass
public class RatingMapper {
    public Rating toRating(Event event, User user, Long rating) {
        return Rating.builder()
                .event(event)
                .user(user)
                .rating(rating)
                .build();
    }

    public RatingDto toRatingDto(Rating rating, Event event, User user) {
        return RatingDto.builder()
                .id(rating.getId())
                .event(EventMapper.toEventShortDto(event))
                .user(UserMapper.toUserShortDto(user))
                .rating(rating.getRating())
                .build();
    }

    public RatingDtoEvent toRatingDtoEvent(Long rating, Event event) {
        return RatingDtoEvent.builder()
                .rating(rating)
                .event(EventMapper.toEventShortDto(event))
                .build();
    }

    public RatingDtoEvent toRatingDtoEvent(EventDtoRating eventDtoRating) {
        return RatingDtoEvent.builder()
                .rating(eventDtoRating.getRating())
                .event(EventMapper.toEventShortDto(eventDtoRating.getEvent()))
                .build();
    }

    public RatingDtoUser toRatingDtoUser(Long rating, User user) {
        return RatingDtoUser.builder()
                .rating(rating)
                .user(UserMapper.toUserShortDto(user))
                .build();
    }

    public RatingDtoEventShort toRatingDtoEventShort(Event event, Long rating) {
        return RatingDtoEventShort.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .rating(rating)
                .build();
    }
}
