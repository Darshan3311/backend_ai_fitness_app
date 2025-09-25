package com.djcode.fitness.fitnessapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Running plan response")
public record RunningPlanResponse(
        @Schema(description = "Ordered list of training weeks") List<RunningWeek> weeks
) {}

