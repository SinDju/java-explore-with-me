package ru.practicum.service;

import ru.practicum.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEventFromAdmin(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest inputUpdate);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto input);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByUsersIdAndEventIdFromUser(Long userId, Long eventId, UpdateEventUserRequest inputUpdate);

    List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest inputUpdate);

    List<EventShortDto> getAllEventFromPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}
