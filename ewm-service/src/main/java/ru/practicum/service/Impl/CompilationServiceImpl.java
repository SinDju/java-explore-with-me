package ru.practicum.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.CompilationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(compilationDto);
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (compilationDto.getEvents() != null) {
            final List<Event> getEvent = eventRepository.findAllById(compilationDto.getEvents());
            compilation.setEvents(getEvent);
        } else {
            compilation.setEvents(new ArrayList<>());
        }
        final Compilation compilationAfterSave = compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilationAfterSave);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto update) {
        final Compilation compilation = checkCompilation(compId);
        if (update.getEvents() != null) {
            compilation.setEvents(update.getEvents().stream()
                    .flatMap(ids -> eventRepository.findAllById(Collections.singleton(ids))
                            .stream())
                    .collect(Collectors.toList()));
        }
        compilation.setPinned(update.getPinned() != null ? update.getPinned() : compilation.getPinned());
        compilation.setTitle(update.getTitle() != null ? update.getTitle() : compilation.getTitle());
        final Compilation compilationAfterSave = compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilationAfterSave);
    }

    @Override
    public void deleteCompilation(Long compId) {
        checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return compilationRepository.findAllByPinnedIs(pinned, pageRequest)
                .stream().map(CompilationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto findByIdCompilation(Long compId) {
        return CompilationMapper.toDto(checkCompilation(compId));
    }

    private Compilation checkCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException("Compilation с ID " + compId + " не найден"));
    }
}
