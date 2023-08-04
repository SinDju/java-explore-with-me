package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.model.Event;
import ru.practicum.model.RatingsLikesOrDislikes;
import ru.practicum.model.User;

@UtilityClass
public class RatingsLikesOrDislikesMapper {
    public RatingsLikesOrDislikes toRatingsLikesOrDislikes(User user, Event event, Boolean isPositive) {
        return RatingsLikesOrDislikes.builder()
                .user(user)
                .event(event)
                .isPositive(isPositive)
                .build();
    }
}
