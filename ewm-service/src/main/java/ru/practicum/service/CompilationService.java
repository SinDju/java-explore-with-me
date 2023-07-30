package ru.practicum.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto compilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto update);

    void deleteCompilation(Long compId);

    List<CompilationDto> getCompilations(boolean pinned, int from, int size);

    CompilationDto findByIdCompilation(Long compId);
}
