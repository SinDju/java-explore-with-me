package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "ratings")
public class Rating {
    // Есть рейтинг, он формируется на основании лайков/дизлайков
    // В таблице review_likes я записываю кто из пользователь оставил лайк/дизлайк на конкретное событие
    // Подсчитать рейтинг события я могу - если выгружу из таблицы rating coint, где eventId =? и сум rating
    // Подсчитать рейтинг авторов события я могу - если выгружу из таблицы events все eventId с initiator по переданнному юзеру
    // и потом передать эти eventId в репозиторий для выгрузки  сум rating
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "event_id")
    private Event event;
    @Column(name = "rating", nullable = false)
    private Long rating; // можно реализовать добавление +1 или -1 в маппере
}