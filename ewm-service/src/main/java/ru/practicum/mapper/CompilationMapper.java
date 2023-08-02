package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationDto;
import ru.practicum.model.Compilation;

@UtilityClass
public class CompilationMapper {
    public CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(EventMapper.toEventShortDtoList(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public Compilation toCompilation(NewCompilationDto compilationDto) {
        return Compilation.builder()
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build();
    }

    public Compilation toUpdateCompilation(UpdateCompilationDto update) {
        return Compilation.builder()
                .id(update.getId())
                .pinned(update.getPinned())
                .title(update.getTitle())
                .build();
    }
}
