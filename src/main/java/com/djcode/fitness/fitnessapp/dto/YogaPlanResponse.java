package com.djcode.fitness.fitnessapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Yoga plan response")
public record YogaPlanResponse(
        @Schema(description = "Ordered list of yoga poses forming the session")
        List<YogaPose> poses
) {}

