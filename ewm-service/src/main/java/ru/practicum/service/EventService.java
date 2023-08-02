package ru.practicum.service;

import ru.practicum.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEventFromAdmin(SearchEventParamsAdmin searchEventParamsAdmin);

    EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest inputUpdate);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto input);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByUsersIdAndEventIdFromUser(Long userId, Long eventId, UpdateEventUserRequest inputUpdate);

    List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatusRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest inputUpdate);

    List<EventShortDto> getAllEventFromPublic(SearchEventParams searchEventParams, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}
