package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "users/{userId}/requests")
public class RequestControllerPrivate {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                    @RequestParam(name = "eventId") Long eventId) {
        log.info("POST запрос на создание запроса на участие в событие с ID {} для пользователя с ID {}",
                eventId, userId);
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllRequests(@PathVariable(value = "userId") @Min(0) Long userId) {
        log.info("GET запрос на получение текущих запросов на участие в событиях для пользователя с ID {}", userId);
        return requestService.getRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto canceledRequest(@PathVariable(value = "userId") @Min(0) Long userId,
                                                   @PathVariable(value = "requestId") @Min(0) Long requestId) {
        log.info("PATCH запрос на отмену запроса на участие в событии для пользователя с ID {}", userId);
        return requestService.canceledRequest(userId, requestId);
    }
}
