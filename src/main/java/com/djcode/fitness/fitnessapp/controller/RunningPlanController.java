package com.djcode.fitness.fitnessapp.controller;

import com.djcode.fitness.fitnessapp.dto.RunningPlanRequest;
import com.djcode.fitness.fitnessapp.dto.RunningPlanResponse;
import com.djcode.fitness.fitnessapp.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/running")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "AI Running Plan", description = "Generate structured multi-week running training plans using AI")
public class RunningPlanController {

    private final AIService aiService;

    @PostMapping("/generate")
    @Operation(
            summary = "Generate AI-powered running plan",
            description = """
                    Generates a progressive week-by-week running plan including easy runs, long runs, tempo / interval work, recovery and rest days
                    based on the athlete's goal, timeframe, and current fitness level.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Running plan generated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RunningPlanResponse.class),
                            examples = @ExampleObject(
                                    name = "Running Plan Response",
                                    value = """
                                            {\n  \"weeks\": [\n    {\n      \"weekNumber\": 1,\n      \"sessions\": [\n        {\n          \"day\": \"Mon\",\n          \"type\": \"Rest\",\n          \"distance\": \"-\",\n          \"notes\": \"Recovery / mobility\"\n        }\n      ]\n    }\n  ]\n}\n                                            """
                            )
                    )
            )
    })
    public ResponseEntity<RunningPlanResponse> generateRunningPlan(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Running plan generation parameters",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RunningPlanRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "5k in 8 weeks (Beginner)",
                                            value = "{\"goal\":\"Run a 5k\",\"timeframe\":\"8 weeks\",\"fitnessLevel\":\"beginner\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Improve pace in 6 weeks (Intermediate)",
                                            value = "{\"goal\":\"Improve my pace\",\"timeframe\":\"6 weeks\",\"fitnessLevel\":\"intermediate\"}"
                                    )
                            }
                    )
            )
            @RequestBody RunningPlanRequest request) {
        return ResponseEntity.ok(aiService.generateRunningPlan(request));
    }
}

