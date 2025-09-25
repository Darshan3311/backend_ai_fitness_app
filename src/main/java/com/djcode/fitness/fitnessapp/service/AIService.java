package com.djcode.fitness.fitnessapp.service;

import com.djcode.fitness.fitnessapp.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.djcode.fitness.fitnessapp.config.AIConfig.GeminiClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIService {

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WorkoutResponse generateWorkout(WorkoutRequest request) {
        log.info("Generating workout for: targetMuscle={}, duration={}, fitnessLevel={}",
                request.targetMuscle(), request.durationInMinutes(), request.fitnessLevel());

        String prompt = buildWorkoutPrompt(request);
        log.debug("Workout prompt: {}", prompt);

        String response = callGemini(prompt, "workout");
        if (response != null) {
            try {
                WorkoutResponse parsed = parseWorkoutResponse(response);
                log.info("Generated workout ({} exercises) via Gemini", parsed.exercises().size());
                return parsed;
            } catch (Exception ex) {
                log.warn("Failed to parse Gemini workout JSON: {}", ex.getMessage());
                log.debug("Raw response: {}", response);
            }
        }
        log.warn("Falling back to dynamic workout fallback");
        return createDynamicFallbackWorkout(request);
    }

    public DietResponse generateDiet(DietRequest request) {
        log.info("Generating diet for: preference={}, goal={}, calories={}, allergies={}",
                request.dietaryPreference(), request.fitnessGoal(), request.dailyCalories(), request.allergies());

        String prompt = buildDietPrompt(request);
        log.debug("Diet prompt: {}", prompt);

        String response = callGemini(prompt, "diet");
        if (response != null) {
            try {
                DietResponse parsed = parseDietResponse(response);
                log.info("Generated diet ({} meals) via Gemini", parsed.meals().size());
                return parsed;
            } catch (Exception ex) {
                log.warn("Failed to parse Gemini diet JSON: {}", ex.getMessage());
                log.debug("Raw response: {}", response);
            }
        }
        log.warn("Falling back to dynamic diet fallback");
        return createDynamicFallbackDiet(request);
    }

    // NEW: Yoga Plan Generation
    public YogaPlanResponse generateYogaPlan(YogaPlanRequest request) {
        log.info("Generating yoga plan: goal={}, duration={}m", request.goal(), request.durationInMinutes());
        String prompt = buildYogaPrompt(request);
        String response = callGemini(prompt, "yoga");
        if (response != null) {
            try {
                YogaPlanResponse parsed = parseYogaPlanResponse(response);
                log.info("Generated yoga plan ({} poses) via Gemini", parsed.poses().size());
                return parsed;
            } catch (Exception ex) {
                log.warn("Failed to parse Gemini yoga JSON: {}", ex.getMessage());
                log.debug("Raw yoga response: {}", response);
            }
        }
        log.warn("Falling back to dynamic yoga plan fallback");
        return createFallbackYogaPlan(request);
    }

    // NEW: Running Plan Generation
    public RunningPlanResponse generateRunningPlan(RunningPlanRequest request) {
        log.info("Generating running plan: goal={}, timeframe={}, level={}", request.goal(), request.timeframe(), request.fitnessLevel());
        String prompt = buildRunningPlanPrompt(request);
        String response = callGemini(prompt, "runningPlan");
        if (response != null) {
            try {
                RunningPlanResponse parsed = parseRunningPlanResponse(response);
                log.info("Generated running plan ({} weeks) via Gemini", parsed.weeks().size());
                return parsed;
            } catch (Exception ex) {
                log.warn("Failed to parse Gemini running plan JSON: {}", ex.getMessage());
                log.debug("Raw running plan response: {}", response);
            }
        }
        log.warn("Falling back to dynamic running plan fallback");
        return createFallbackRunningPlan(request);
    }

    private String callGemini(String prompt, String type) {
        String result = geminiClient.generate(prompt);
        if (result == null) {
            log.error("Gemini returned null for {} (check API key / network)", type);
        } else {
            log.debug("Gemini raw {} response: {}", type, result);
        }
        return result;
    }

    private String buildWorkoutPrompt(WorkoutRequest request) {
        return String.format("""
            You are an expert fitness trainer. Create a personalized %d-minute workout routine targeting %s muscles for someone at %s fitness level.
            Respond ONLY with valid JSON (no markdown) in this exact format:
            {"exercises":[{"name":"...","sets":"3","reps":"10-12","description":"..."}]}
            4-6 exercises. Progressive, safe, concise descriptions. No extra keys.
            """,
            request.durationInMinutes(),
            request.targetMuscle(),
            request.fitnessLevel()
        );
    }

    private String buildDietPrompt(DietRequest request) {
        return String.format("""
            You are a professional nutritionist. Build a one-day meal plan.
            Preference:%s Goal:%s Calories:%d Allergies:%s
            Respond ONLY with JSON: {"meals":[{"name":"...","ingredients":"...","calories":"...","description":"..."}]}
            Include breakfast, lunch, dinner and 1-2 snacks. Sum close to %d calories. No extra commentary.
            """,
            request.dietaryPreference(),
            request.fitnessGoal(),
            request.dailyCalories(),
            request.allergies(),
            request.dailyCalories()
        );
    }

    // NEW prompt builders
    private String buildYogaPrompt(YogaPlanRequest request) {
        return String.format("""
            You are a certified yoga instructor. Create a %d-minute yoga flow focused on the goal: %s.
            Provide 6-10 sequential poses with mindful transitions. Keep pose names standard.
            Respond ONLY with JSON: {"poses":[{"name":"Mountain Pose","hold":"60 sec","description":"Brief clear guidance"}]}.
            Each pose requires: name, hold (seconds or breaths), description (succinct alignment & breathing cues). No extra keys.
            """,
            request.durationInMinutes(),
            request.goal()
        );
    }

    private String buildRunningPlanPrompt(RunningPlanRequest request) {
        return String.format("""
            You are an experienced running coach. Create a structured week-by-week running plan to achieve goal: %s within %s.
            Athlete level: %s.
            Include variety: easy runs, long runs, interval/tempo work, recovery, and rest days.
            Respond ONLY with JSON: {"weeks":[{"weekNumber":1,"sessions":[{"day":"Mon","type":"Easy Run","distance":"3 km","notes":"Conversational pace"}]}]}.
            Distance units concise (km). 5-7 sessions per week, include at least one rest day. No commentary outside JSON.
            """,
            request.goal(), request.timeframe(), request.fitnessLevel()
        );
    }

    // NEW parse helpers
    private YogaPlanResponse parseYogaPlanResponse(String response) throws JsonProcessingException {
        String json = extractJsonFromResponse(response);
        return objectMapper.readValue(json, YogaPlanResponse.class);
    }

    private RunningPlanResponse parseRunningPlanResponse(String response) throws JsonProcessingException {
        String json = extractJsonFromResponse(response);
        return objectMapper.readValue(json, RunningPlanResponse.class);
    }

    // Original parse helpers for workout & diet (restored)
    private WorkoutResponse parseWorkoutResponse(String response) throws JsonProcessingException {
        String json = extractJsonFromResponse(response);
        return objectMapper.readValue(json, WorkoutResponse.class);
    }

    private DietResponse parseDietResponse(String response) throws JsonProcessingException {
        String json = extractJsonFromResponse(response);
        return objectMapper.readValue(json, DietResponse.class);
    }

    private String extractJsonFromResponse(String response) {
        if (response == null || response.isBlank()) {
            throw new RuntimeException("Empty response");
        }
        String cleaned = response
                .replace("```json", "")
                .replace("```", "")
                .trim();
        int s = cleaned.indexOf('{');
        int e = cleaned.lastIndexOf('}');
        if (s >= 0 && e > s) {
            return cleaned.substring(s, e + 1);
        }
        throw new RuntimeException("No JSON brackets found");
    }

    // Fallback generation (unchanged below)
    private WorkoutResponse createDynamicFallbackWorkout(WorkoutRequest request) {
        List<Exercise> exercises;
        switch (request.targetMuscle().toLowerCase()) {
            case "chest": exercises = createChestExercises(request.fitnessLevel()); break;
            case "back": exercises = createBackExercises(request.fitnessLevel()); break;
            case "legs": exercises = createLegExercises(request.fitnessLevel()); break;
            case "arms": exercises = createArmExercises(request.fitnessLevel()); break;
            default: exercises = createFullBodyExercises(request.fitnessLevel());
        }
        return new WorkoutResponse(exercises);
    }

    private DietResponse createDynamicFallbackDiet(DietRequest request) {
        List<Meal> meals;
        switch (request.dietaryPreference().toLowerCase()) {
            case "vegetarian": meals = createVegetarianMeals(request); break;
            case "vegan": meals = createVeganMeals(request); break;
            case "keto": meals = createKetoMeals(request); break;
            default: meals = createBalancedMeals(request);
        }
        return new DietResponse(meals);
    }

    // NEW fallback generators
    private YogaPlanResponse createFallbackYogaPlan(YogaPlanRequest request) {
        List<YogaPose> poses = List.of(
                new YogaPose("Centering Breath", "60 sec", "Seated or standing, deepen breathing to settle"),
                new YogaPose("Cat-Cow", "6 breaths", "Alternate spinal flexion/extension with inhales and exhales"),
                new YogaPose("Downward Dog", "60 sec", "Press through palms, lengthen spine, soften knees"),
                new YogaPose("Low Lunge", "45 sec each", "Front knee over ankle, hips square, steady breath"),
                new YogaPose("Warrior II", "45 sec each", "Front knee bent, arms extended, gaze over front hand"),
                new YogaPose("Triangle", "45 sec each", "Straighten front leg, hinge at hip, lengthen both sides"),
                new YogaPose("Seated Forward Fold", "60 sec", "Lengthen spine on inhale, fold gently on exhale"),
                new YogaPose("Supine Twist", "45 sec each", "Arms wide, shoulders grounded, gentle spinal rotation"),
                new YogaPose("Savasana", "2 min", "Relax fully, natural breath, release tension")
        );
        return new YogaPlanResponse(poses);
    }

    private RunningPlanResponse createFallbackRunningPlan(RunningPlanRequest request) {
        int weeks = extractWeeks(request.timeframe());
        if (weeks <= 0) weeks = 4;
        // Simple progressive plan
        List<RunningWeek> weekList = new java.util.ArrayList<>();
        int baseEasy = 3; // km
        for (int w = 1; w <= weeks; w++) {
            int easy = baseEasy + (w - 1);
            int longRun = easy + 4;
            List<RunningSession> sessions = List.of(
                    new RunningSession("Mon", "Rest", "-", "Recovery / mobility"),
                    new RunningSession("Tue", "Easy Run", easy + " km", "Comfortable pace"),
                    new RunningSession("Wed", "Intervals", (easy - 1) + " km", "Short repeats / speed focus"),
                    new RunningSession("Thu", "Easy Run", easy + " km", "Steady aerobic"),
                    new RunningSession("Fri", "Rest", "-", "Sleep & nutrition focus"),
                    new RunningSession("Sat", "Tempo", (easy + 1) + " km", "Sustained comfortably hard"),
                    new RunningSession("Sun", "Long Run", longRun + " km", "Endurance building")
            );
            weekList.add(new RunningWeek(w, sessions));
        }
        return new RunningPlanResponse(weekList);
    }

    private int extractWeeks(String timeframe) {
        if (timeframe == null) return 0;
        try {
            String digits = timeframe.trim().split(" ")[0];
            return Integer.parseInt(digits);
        } catch (Exception e) {
            return 0;
        }
    }

    // Restored fallback helper methods for workouts
    private List<Exercise> createChestExercises(String level) {
        if ("beginner".equalsIgnoreCase(level)) {
            return List.of(
                new Exercise("Wall Push-ups", "3", "8-12", "Beginner-friendly chest exercise against wall"),
                new Exercise("Incline Push-ups", "3", "6-10", "Push-ups with hands elevated on bench"),
                new Exercise("Knee Push-ups", "3", "5-8", "Modified push-ups from knees"),
                new Exercise("Chest Squeeze", "3", "10-15", "Isometric chest contraction exercise")
            );
        } else if ("intermediate".equalsIgnoreCase(level)) {
            return List.of(
                new Exercise("Standard Push-ups", "3", "10-15", "Classic bodyweight chest exercise"),
                new Exercise("Wide-Grip Push-ups", "3", "8-12", "Push-ups with wider hand placement"),
                new Exercise("Diamond Push-ups", "3", "6-10", "Push-ups with hands in diamond shape"),
                new Exercise("Decline Push-ups", "3", "8-12", "Push-ups with feet elevated")
            );
        } else {
            return List.of(
                new Exercise("One-Arm Push-ups", "3", "3-6", "Advanced single-arm push-up variation"),
                new Exercise("Archer Push-ups", "3", "5-8", "Single-sided push-up movement"),
                new Exercise("Explosive Push-ups", "4", "6-10", "Plyometric push-up with hand clap"),
                new Exercise("Hindu Push-ups", "3", "8-12", "Dynamic flowing push-up movement")
            );
        }
    }

    private List<Exercise> createBackExercises(String level) {
        return List.of(
            new Exercise("Superman", "3", "10-15", "Lying back extension exercise"),
            new Exercise("Reverse Fly", "3", "12-15", "Rear deltoid and upper back exercise"),
            new Exercise("Bird Dog", "3", "10 each side", "Core and back stability exercise"),
            new Exercise("Good Mornings", "3", "12-15", "Hip hinge movement for lower back")
        );
    }

    private List<Exercise> createLegExercises(String level) {
        return List.of(
            new Exercise("Bodyweight Squats", "3", "12-20", "Basic lower body exercise"),
            new Exercise("Lunges", "3", "10 each leg", "Single-leg strength exercise"),
            new Exercise("Calf Raises", "3", "15-20", "Lower leg strengthening exercise"),
            new Exercise("Wall Sit", "3", "30-60 sec", "Isometric quad strengthening")
        );
    }

    private List<Exercise> createArmExercises(String level) {
        return List.of(
            new Exercise("Tricep Dips", "3", "8-12", "Bodyweight tricep exercise"),
            new Exercise("Pike Push-ups", "3", "6-10", "Shoulder and tricep focused exercise"),
            new Exercise("Arm Circles", "3", "15 each direction", "Shoulder mobility and strength"),
            new Exercise("Plank to Push-up", "3", "8-12", "Dynamic arm and core exercise")
        );
    }

    private List<Exercise> createFullBodyExercises(String level) {
        return List.of(
            new Exercise("Burpees", "3", "8-12", "Full body high-intensity exercise"),
            new Exercise("Mountain Climbers", "3", "20-30", "Dynamic full body cardio exercise"),
            new Exercise("Jumping Jacks", "3", "15-25", "Full body cardiovascular exercise"),
            new Exercise("Plank", "3", "30-60 sec", "Core stability exercise")
        );
    }

    // Restored meal fallback creators
    private List<Meal> createVegetarianMeals(DietRequest request) {
        return List.of(
            new Meal("Veggie Protein Bowl", "Quinoa, black beans, avocado, spinach", "400", "High-protein vegetarian breakfast"),
            new Meal("Lentil Salad", "Green lentils, cucumber, tomato, feta", "350", "Protein-rich lunch option"),
            new Meal("Stuffed Bell Peppers", "Peppers, rice, cheese, herbs", "450", "Nutritious vegetarian dinner"),
            new Meal("Greek Yogurt with Nuts", "Greek yogurt, almonds, berries", "200", "Protein-packed snack")
        );
    }

    private List<Meal> createVeganMeals(DietRequest request) {
        return List.of(
            new Meal("Chia Seed Pudding", "Chia seeds, almond milk, banana", "350", "Plant-based protein breakfast"),
            new Meal("Buddha Bowl", "Tofu, quinoa, kale, tahini dressing", "450", "Complete vegan lunch"),
            new Meal("Lentil Curry", "Red lentils, coconut milk, vegetables", "400", "Hearty vegan dinner"),
            new Meal("Hummus with Veggies", "Hummus, carrots, bell peppers", "180", "Plant-based snack")
        );
    }

    private List<Meal> createKetoMeals(DietRequest request) {
        return List.of(
            new Meal("Avocado Eggs", "Eggs, avocado, bacon, cheese", "450", "High-fat keto breakfast"),
            new Meal("Keto Caesar Salad", "Romaine, chicken, parmesan, keto dressing", "400", "Low-carb lunch"),
            new Meal("Salmon with Asparagus", "Salmon, asparagus, butter sauce", "500", "Keto-friendly dinner"),
            new Meal("Keto Fat Bombs", "Coconut oil, nuts, cocoa", "200", "High-fat keto snack")
        );
    }

    private List<Meal> createBalancedMeals(DietRequest request) {
        return List.of(
            new Meal("Balanced Breakfast", "Oatmeal, berries, protein powder, nuts", "350", "Well-rounded morning meal"),
            new Meal("Chicken Quinoa Bowl", "Grilled chicken, quinoa, mixed vegetables", "450", "Balanced lunch option"),
            new Meal("Lean Protein Dinner", "Fish, sweet potato, broccoli", "500", "Balanced evening meal"),
            new Meal("Mixed Nuts", "Almonds, walnuts, dried fruit", "200", "Healthy balanced snack")
        );
    }
}
