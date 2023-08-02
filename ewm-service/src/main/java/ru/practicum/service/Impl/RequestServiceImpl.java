package ru.practicum.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.enums.EventStatus;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ParametersException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("Пользователь с ID " + userId + " не найден или недоступен"));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Событие с ID " + eventId + " не найдено или недоступно"));
        LocalDateTime createdOn = LocalDateTime.now();
        validationNewRequest(event, userId, eventId);
        Request request = new Request();
        request.setCreated(createdOn);
        request.setRequester(user);
        request.setEvent(event);
        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else request.setStatus(RequestStatus.CONFIRMED);
        requestRepository.save(request);
        int countRequestConfirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getConfirmedRequests() != countRequestConfirmed) {
            event.setConfirmedRequests(countRequestConfirmed);
            eventRepository.save(event);
        }
        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        checkUser(userId);
        List<Request> result = requestRepository.findAllByRequesterId(userId);
        return result.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto canceledRequest(Long userId, Long requestId) {
        checkUser(userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(
                () -> new ObjectNotFoundException("Запрос с ID " + requestId + " не найден или недоступно"));
        if (request.getStatus().equals(RequestStatus.CANCELED) || request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new ParametersException("Request не подтвержден");
        }
        request.setStatus(RequestStatus.CANCELED);
        Request requestAfterSave = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(requestAfterSave);
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("Пользователь с ID " + userId + " не найден или недоступен");
        }
    }

    private void validationNewRequest(Event event, Long userId, Long eventId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("У пользователя с ID " + userId + " нет доступа");
        }
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new ConflictException("Заполнен лимит участников события");
        }
        if (!event.getEventStatus().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Событие не опубкликованно");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Cannot add duplicate request");
        }
    }
}
