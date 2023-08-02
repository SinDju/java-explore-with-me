package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.SearchEventParamsAdmin;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/events")
public class EventControllerAdmin {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> searchEvents(@Valid SearchEventParamsAdmin searchEventParamsAdmin) {
        log.info("GET запрос на получение списка событий");
        return eventService.getAllEventFromAdmin(searchEventParamsAdmin);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable(value = "eventId") @Min(1) Long eventId,
                                           @RequestBody @Valid UpdateEventAdminRequest inputUpdate) {
        log.info("PATCH запрос на редактирование списка событий");
        return eventService.updateEventFromAdmin(eventId, inputUpdate);
    }
}
