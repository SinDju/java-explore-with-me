package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    private Long id;

    @NotEmpty
    private String app;

    @NotEmpty
    private String uri;

    @NotEmpty
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Дата не задана")
    private LocalDateTime timestamp;
}
