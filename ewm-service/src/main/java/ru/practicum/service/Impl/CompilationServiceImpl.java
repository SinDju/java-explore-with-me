package ru.practicum.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationDto;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.service.CompilationService;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        return null;
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto update) {
        return null;
    }

    @Override
    public void deleteCompilation(Long compId) {

    }

    @Override
    public CompilationDto getCompilations(Boolean pinned, Integer from, Integer size) {
        return null;
    }

    @Override
    public CompilationDto findByIdCompilation(Long compId) {
        return null;
    }
}
