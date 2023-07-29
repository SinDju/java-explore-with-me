package ru.practicum.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateCompilationDto {
    private Long id;
    private List<Long> events;
    private Boolean pinned;
    @Size(max = 50)
    private String title;
}
