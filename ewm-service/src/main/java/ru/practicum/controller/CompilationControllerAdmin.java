package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationDto;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/compilations")
public class CompilationControllerAdmin {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("POST запрос на создание подборки событий");
        return compilationService.addCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@RequestBody @Valid UpdateCompilationDto update,
                                            @PathVariable Long compId) {
        log.info("PATCH запрос на обнавление подборки событий");
        return compilationService.updateCompilation(compId, update);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("DELETE запрос на удаление подборки событий");
        compilationService.deleteCompilation(compId);
    }
}
