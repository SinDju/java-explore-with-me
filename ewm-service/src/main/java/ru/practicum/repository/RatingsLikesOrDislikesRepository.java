package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.RatingsLikesOrDislikes;

public interface RatingsLikesOrDislikesRepository extends JpaRepository<RatingsLikesOrDislikes, Long> {

}
