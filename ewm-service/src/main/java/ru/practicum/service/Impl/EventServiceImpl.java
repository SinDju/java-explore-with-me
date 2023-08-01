package ru.practicum.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.EndpointHitClient;
import ru.practicum.dto.*;
import ru.practicum.enums.EventStatus;
import ru.practicum.enums.RequestStatus;
import ru.practicum.enums.StateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ParametersException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.*;
import ru.practicum.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EndpointHitClient statClient;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<EventFullDto> getAllEventFromAdmin(List<Long> users, List<String> states, List<Long> categories,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                                   Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        Specification<Event> specification = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("initiator").get("id").in(users));
        }
        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("eventStatus").as(String.class).in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        Page<Event> events = eventRepository.findAll(specification, pageable);
        return events.getContent().stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventFromAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event oldEvent = checkEvent(eventId);
        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED) || oldEvent.getEventStatus().equals(EventStatus.CANCELED)) {
            throw new ConflictException("Можно изменить только неподтвержденное событие");
        }
        int updatePoint = 0;
        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = checkCategory(updateEvent.getCategory());
            oldEvent.setCategory(category);
            updatePoint = 1;
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isEmpty()) {
            oldEvent.setDescription(updateEvent.getDescription());
            updatePoint = 1;
        }
        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ParametersException("Дата начала изменяемого события должна " +
                        "быть не ранее чем за час от даты публикации.");
            }
            oldEvent.setEventDate(updateEvent.getEventDate());
            updatePoint = 1;
        }
        if (updateEvent.getLocation() != null) {
           if (oldEvent.getLocation() == null) {
               Location location = locationRepository.save(updateEvent.getLocation());
               oldEvent.setLocation(location);
           }
           oldEvent.setLocation(updateEvent.getLocation());
            updatePoint = 1;
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
            updatePoint = 1;
        }
        if (updateEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
            updatePoint = 1;
        }
        if (updateEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEvent.getRequestModeration());
            updatePoint = 1;
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                oldEvent.setEventStatus(EventStatus.PUBLISHED);
                updatePoint = 1;
            } else if (updateEvent.getStateAction().equals(StateAction.REJECT_EVENT)) {
                oldEvent.setEventStatus(EventStatus.CANCELED);
                updatePoint = 1;
            }
        }
        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            oldEvent.setTitle(updateEvent.getTitle());
        }
        Event eventAfterUpdate = null;
        if (updatePoint > 0) eventAfterUpdate = eventRepository.save(oldEvent);
        return eventAfterUpdate != null ? EventMapper.toEventFullDto(eventAfterUpdate) : null;
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь с ID " + userId + " не найден или недоступен");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return eventRepository.findAll(pageRequest).getContent()
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto eventDto) {
        LocalDateTime createdOn = LocalDateTime.now();
        User user = checkUser(userId);
        checkDateTime(LocalDateTime.now(), eventDto.getEventDate());
        Category category = checkCategory(eventDto.getCategory());
        Location location = locationRepository.save(eventDto.getLocation());
        Event event = EventMapper.toEvent(eventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setEventStatus(EventStatus.PENDING);
        event.setCreatedDate(createdOn);
        event.setConfirmedRequests(0);
        event.setViews(0);
        event.setLocation(location);
        Event eventSaved = eventRepository.save(event);
        return EventMapper.toEventFullDto(eventSaved);
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        checkUser(userId);
        Event event = checkEvenByInitiatorAndEventId(userId, eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUsersIdAndEventIdFromUser(Long userId, Long eventId,
                                                               UpdateEventUserRequest inputUpdate) {
        checkUser(userId);
        int updatePoint = 0;
        Event oldEvent = checkEvenByInitiatorAndEventId(userId, eventId);
        if (inputUpdate.getEventDate() != null) {
            LocalDateTime newDate = inputUpdate.getEventDate();
            checkDateTime(LocalDateTime.now(), newDate);
            oldEvent.setEventDate(newDate);
            updatePoint++;
        }
        if (!oldEvent.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователя с ID " + userId + " не явлеятсе создателем собития");
        }
        if (oldEvent.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Статус события не может быть обнавлен, так как оно уже опубликовано");
        }
        if (inputUpdate.getAnnotation() != null && !inputUpdate.getAnnotation().isBlank()) {
            oldEvent.setAnnotation(inputUpdate.getAnnotation());
            updatePoint++;
        }
        if (inputUpdate.getCategory() != null) {
            Category category = checkCategory(inputUpdate.getCategory());
            oldEvent.setCategory(category);
            updatePoint++;
        }
        if (inputUpdate.getDescription() != null) {
            oldEvent.setDescription(inputUpdate.getDescription());
            updatePoint++;
        }
        if (inputUpdate.getLocation() != null) {
            oldEvent.setLocation(inputUpdate.getLocation());
            updatePoint++;
        }
        if (inputUpdate.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(inputUpdate.getParticipantLimit());
            updatePoint++;
        }
        if (inputUpdate.getRequestModeration() != null) {
            oldEvent.setRequestModeration(inputUpdate.getRequestModeration());
            updatePoint++;
        }
        if (inputUpdate.getStateAction() != null) {
            switch (inputUpdate.getStateAction()) {
                case SEND_TO_REVIEW:
                    oldEvent.setEventStatus(EventStatus.PENDING);
                    updatePoint++;
                    break;
                case CANCEL_REVIEW:
                    oldEvent.setEventStatus(EventStatus.CANCELED);
                    updatePoint++;
                    break;
            }
        }
        if (inputUpdate.getTitle() != null) {
            oldEvent.setTitle(inputUpdate.getTitle());
            updatePoint++;
        }
        Event eventAfterUpdate = null;
        if (updatePoint > 0) {
            eventAfterUpdate = eventRepository.save(oldEvent);
        }
        return eventAfterUpdate != null ? EventMapper.toEventFullDto(eventAfterUpdate) : null;
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestsFromEventByOwner(Long userId, Long eventId) {
        checkUser(userId);
        checkEvenByInitiatorAndEventId(userId, eventId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateStatusRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest inputUpdate) {
        checkUser(userId);
        Event event = checkEvenByInitiatorAndEventId(userId, eventId);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Это событие не требует подтверждения запросов");
        }
        RequestStatus status = inputUpdate.getStatus();

        switch (status) {
            case CONFIRMED:
                if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                    throw new ConflictException("Лимит участников исчерпан");
                }
                CaseUpdatedStatusDto updatedStatusConfirmed = updatedStatusConfirmed(event, CaseUpdatedStatusDto.builder()
                        .idsFromUpdateStatus(inputUpdate.getRequestIds()).build(), RequestStatus.CONFIRMED);

                List<Request> confirmedRequests = requestRepository.findAllById(updatedStatusConfirmed.getProcessedIds());
                List<Request> rejectedRequests = new ArrayList<>();
                if (updatedStatusConfirmed.getIdsFromUpdateStatus().size() != 0) {
                    List<Long> ids = updatedStatusConfirmed.getIdsFromUpdateStatus();
                    rejectedRequests = rejectOtherRequest(ids, eventId);
                }

                return EventRequestStatusUpdateResult.builder()
                        .confirmedRequests(confirmedRequests
                                .stream()
                                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                        .rejectedRequests(rejectedRequests
                                .stream()
                                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                        .build();
            case REJECTED:
                if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                    throw new ConflictException("Лимит участников исчерпан");
                }

                final CaseUpdatedStatusDto updatedStatusReject = updatedStatusConfirmed(event, CaseUpdatedStatusDto.builder()
                        .idsFromUpdateStatus(inputUpdate.getRequestIds()).build(), RequestStatus.REJECTED);
                List<Request> rejectRequest = requestRepository.findAllById(updatedStatusReject.getProcessedIds());

                return EventRequestStatusUpdateResult.builder()
                        .rejectedRequests(rejectRequest
                                .stream()
                                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                        .build();
            default:
                throw new ParametersException("Некорректный статус - " + status);
        }
    }

    @Override
    public List<EventShortDto> getAllEventFromPublic(String text, List<Long> categories, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable, String sort, Integer from,
                                                     Integer size, HttpServletRequest request) {
        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ParametersException("Error: Дата окончания находится до даты начала");
            }
        }
        addStatsClient(request);
        Pageable pageable = PageRequest.of(from / size, size);
        Specification<Event> specification = Specification.where(null);

        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("eventStatus"), EventStatus.PUBLISHED));

        List<Event> resultEvents = eventRepository.findAll(specification, pageable).getContent();
        getViewsOfEvents(resultEvents);

        return resultEvents.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = checkEvent(eventId);
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ObjectNotFoundException("События с ID " + eventId + " не опубликованно");
        }
        addStatsClient(request);
        getViewsOfEvents(List.of(event));
        return EventMapper.toEventFullDto(event);
    }

    private void checkDateTime(LocalDateTime time, LocalDateTime dateTimeDto) {
        if (dateTimeDto.isBefore(time.plusHours(2))) {
            throw new ParametersException("Error: должно содержать дату, которая еще не наступила.");
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь с ID " + userId + " не найден или недоступен"));
    }

    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(
                () -> new ObjectNotFoundException("Категории с ID " + catId + " не существует"));
    }

    private Request checkRequestOrEvent(Long eventId, Long requestId) {
        return requestRepository.findByEventIdAndId(eventId, requestId).orElseThrow(
                () -> new ObjectNotFoundException("Запроса с ID " + requestId + " не существует или события с ID "
                        + eventId));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("События с ID " + eventId + " не существует"));
    }

    private Event checkEvenByInitiatorAndEventId(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new ObjectNotFoundException("События с ID " + eventId + "и с пользователем с ID" + userId +
                        " не существует"));
    }

    private CaseUpdatedStatusDto updatedStatusConfirmed(Event event, CaseUpdatedStatusDto caseUpdatedStatus, RequestStatus status) {
        Long eventId = event.getId();
        List<Long> ids = caseUpdatedStatus.getIdsFromUpdateStatus();
        int idsSize = caseUpdatedStatus.getIdsFromUpdateStatus().size();
        List<Long> processedIds = new ArrayList<>();
        int freeRequest = event.getParticipantLimit() - event.getConfirmedRequests();
        for (int i = 0; i < idsSize; i++) {
            final Request request = checkRequestOrEvent(eventId, ids.get(i));
            if (freeRequest == 0) {
                break;
            }
            request.setStatus(status);
            requestRepository.save(request);
            Long confirmedId = request.getId();
            processedIds.add(confirmedId);
            freeRequest--;
        }
        Integer confirmedRequestUpdate = event.getConfirmedRequests() + processedIds.size();
        event.setConfirmedRequests(confirmedRequestUpdate);
        eventRepository.save(event);
        caseUpdatedStatus.setIdsFromUpdateStatus(ids);
        caseUpdatedStatus.setProcessedIds(processedIds);
        return caseUpdatedStatus;
    }

    private List<Request> rejectOtherRequest(List<Long> ids, Long eventId) {
        List<Request> rejectedRequests;
        int sizeIds = ids.size();
        for (int i = 0; i < sizeIds; i++) {
            final Request request = checkRequestOrEvent(eventId, ids.get(i));
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                break;
            }
            request.setStatus(RequestStatus.REJECTED);
            requestRepository.save(request);
        }
        rejectedRequests = requestRepository.findAllById(ids);
        return rejectedRequests;
    }

    private void addStatsClient(HttpServletRequest request) {
        String app = "ewm-service";
        statClient.postStats(EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private void getViewsOfEvents(List<Event> events) {
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());

        ResponseEntity<Object> response = statClient.getStats("2000-01-01 00:00:00", "2100-01-01 00:00:00",
                uris, false);

        final ObjectMapper mapper = new ObjectMapper();
        final List<ViewStatsDto> viewStatsList = mapper.convertValue(response.getBody(), new TypeReference<List<ViewStatsDto>>() {
        });

        for (Event event : events) {
            ViewStatsDto currentViewStats = viewStatsList.stream()
                    .filter(statsDto -> {
                        Long eventIdOfViewStats = Long.parseLong(statsDto.getUri().substring("/events/".length()));
                        return eventIdOfViewStats.equals(event.getId());
                    })
                    .findFirst()
                    .orElse(null);

            Long views = (currentViewStats != null) ? currentViewStats.getHits() : 0;
            event.setViews(views.intValue() + 1);
        }
        eventRepository.saveAll(events);
    }
}
