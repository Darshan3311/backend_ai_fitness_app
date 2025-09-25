package com.djcode.fitness.fitnessapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "A training week within a running plan")
public record RunningWeek(
        @Schema(example = "1") int weekNumber,
        @Schema(description = "Sessions for the week") List<RunningSession> sessions
) {}

