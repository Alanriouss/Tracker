# 01 — Prediction-Core Integration Report

## 1. Context Before Integration

Academic Tracker originally provided student profile management, semester/course/assessment tracking, and several GPA or grade-related helper calculations. Some of that logic lived directly in the app model:

- `Student.getCumulativeGPA()`
- `Student.getCompletedGPA()`
- `Student.getTotalCreditsCompleted()`
- `Semester.getGPA()`
- `Semester.getAverageScore()`
- `Course.getCurrentGrade()`

That approach is simple at first, but it becomes harder to maintain when prediction logic grows. If controllers or models own too many formulas, the UI layer becomes tightly coupled to business rules. That is the main reason prediction-core was introduced.

## 2. Goals of the prediction-core Integration

The integration had several goals:

- Separate business logic from JavaFX UI code
- Keep controllers focused on UI event handling
- Use prediction-core as the calculation engine
- Add a service layer for app-level prediction use cases
- Add a mapper layer for converting app models into prediction-core DTOs
- Keep the existing JSON data format unchanged

## 3. Main Changes That Were Implemented

### 3.1 Java 21 / Maven configuration

File:

- `pom.xml`

The project now uses:

```xml
<maven.compiler.release>21</maven.compiler.release>
```

Why Java 21 matters:

- The project is configured for Java 21.
- prediction-core uses Java `record` types for DTOs and result objects.
- Java 21 is an LTS release and works well with current JavaFX dependencies.

Maven Wrapper support:

- `mvnw`
- `mvnw.cmd`
- `.mvn/`

This allows teammates to run the app without installing Maven manually.

### 3.2 prediction-core copied into the app

prediction-core now lives inside the main app project:

```text
src/main/java/com/academictracker/prediction2
```

That means the external folder `prediction-core-2.0` is no longer required to run the app. Keeping prediction-core in its own package is still important because it preserves independence from JavaFX, Gson, FXML, and controller code.

### 3.3 PredictionService

File:

- `src/main/java/com/tracker/academictracker/service/PredictionService.java`

Responsibility:

- Provide prediction use cases to controllers
- Use `PredictionMapper` for data conversion
- Use `PredictionCoreFacade` for prediction-core access

Verified methods:

- `calculateCourseGrade(Course course)`
- `predictCourseTarget(Course course, double targetScore)`
- `projectStudentGpa(Student student)`
- `projectSemesterGpa(Semester semester)`
- `calculateRequiredActiveTermGpa(Student student, Semester activeSemester, double targetGpa)`

Benefits:

- Controllers do not need to know about low-level engine classes.
- Prediction use cases are centralized in one app-facing service.

### 3.4 PredictionMapper

File:

- `src/main/java/com/tracker/academictracker/mapper/PredictionMapper.java`

Responsibility:

- Convert app domain models into prediction-core input models

Verified mapping methods:

- `toTranscriptInput(...)`
- `toSemesterInput(...)`
- `toCourseInput(...)`
- `toAssessmentInput(...)`
- `countActiveCredits(...)`

Why the mapper is necessary:

- The app model is designed for UI and JSON persistence.
- prediction-core expects clean DTO inputs.
- The mapper prevents tight coupling between the two layers.

### 3.5 Score normalization

This is one of the most important integration details.

The app stores scores as:

```text
score = achieved points
maxScore = maximum possible points
```

prediction-core expects:

```text
score = a value on the 0-100 scale
```

Example:

```text
score = 8
maxScore = 10
normalized score = 8 / 10 * 100 = 80
```

This conversion is implemented in:

- `PredictionMapper.toAssessmentInput(Assessment assessment)`

Why it is critical:

- If the conversion is skipped, `8/10` would be treated as `8%` instead of `80%`.
- That would break current-grade, target-grade, and GPA calculations.

### 3.6 DashboardController integration

File:

- `src/main/java/com/tracker/academictracker/ui/controller/DashboardController.java`

Prediction-related features now using `PredictionService`:

- `updateOverallGpaLabel()` uses `projectStudentGpa(student)`
- `updateTermWidgets(...)` uses `projectSemesterGpa(activeSemester)`
- `calculateTargetGrades()` uses `calculateRequiredActiveTermGpa(...)`

What this means:

- The dashboard no longer needs to own heavy prediction math directly.
- It receives result objects and formats UI messages.

Error handling:

- The controller catches `NumberFormatException`
- It also handles `PredictionInputException` and `IllegalArgumentException`

### 3.7 CourseDetailsController integration

File:

- `src/main/java/com/tracker/academictracker/ui/controller/CourseDetailsController.java`

Prediction-related features now using `PredictionService`:

- `refreshData()` uses `calculateCourseGrade(currentCourse)`
- `calculateCourseTarget()` uses `predictCourseTarget(currentCourse, targetGrade)`

Status handling:

- `IMPOSSIBLE`
- `ACHIEVABLE`
- `ALREADY_ACHIEVED`

Validation behavior:

- Invalid prediction input is caught and turned into user-friendly UI feedback instead of crashing the app.

### 3.8 Validation improvements

prediction-core validation is centralized in:

- `src/main/java/com/academictracker/prediction2/policy/ValidationPolicy.java`

It handles:

- total assessment weight above 100%
- invalid score values
- invalid target score
- invalid target GPA
- invalid remaining credits

Additional app-side safety:

- `PredictionMapper` rejects invalid `maxScore <= 0`
- `PredictionService.calculateRequiredActiveTermGpa(...)` rejects missing active credits

The UI should catch these cases and show friendly messages instead of failing hard.

## 4. Architecture After Integration

Prediction flow:

```text
JavaFX UI / FXML
  ↓
Controller
  ↓
PredictionService
  ↓
PredictionMapper
  ↓
PredictionCoreFacade
  ↓
Prediction Engines
  ↓
Result Objects
  ↓
UI display
```

Persistence flow:

```text
Controller / App
  ↓
DataManager
  ↓
student_data.json
```

## 5. Benefits of Integration

- Better separation of concerns
- Cleaner OOP design
- Easier testing of prediction logic
- Easier maintenance
- Easier architecture explanation through design patterns
- Reusable business logic
- Less calculation responsibility inside controllers
- prediction-core remains independent from the UI layer

## 6. Trade-offs / Limitations

- More classes and packages
- More abstraction for beginners to learn
- Mapper logic can be confusing at first
- Score conversion must stay accurate
- Java 21 is required
- JavaFX/Maven setup may still confuse some classmates
- Some controllers still contain a lot of UI logic

## 7. How to Verify prediction-core Is Working

### Test 1: Score normalization

Assessment:

- Weight = 50
- Score = 8
- Max score = 10

Expected:

- The current grade should be around 80%, not 8%.

### Test 2: Course target grade

Given:

- Midterm weight = 50
- Score = 8/10 = 80%

Target grade = 90

Expected:

- Required remaining average should be 100% on the remaining 50%.

Target grade = 95

Expected:

- The result should be unreachable/impossible because the required remaining average would be 110%.

### Test 3: Dashboard target GPA

Input a target GPA such as `3.5`.

Expected:

- The app should return one of three outcomes:
  - required term GPA
  - already achieved
  - impossible

### Test 4: Invalid weight

Assessment weights:

- 60
- 60

Expected:

- The app should not crash.
- The UI should show an invalid-data or check-weights warning.

## 8. Most Important Files After Integration

| File path | Role | Why it matters |
|---|---|---|
| `pom.xml` | Maven and Java config | Defines Java 21, JavaFX, tests, and launch settings |
| `src/main/java/com/tracker/academictracker/app/Launcher.java` | App launcher | Entry point used by Maven and IDEs |
| `src/main/java/com/tracker/academictracker/app/Main.java` | JavaFX startup | Decides which first screen to load and initializes the app |
| `src/main/java/com/tracker/academictracker/service/PredictionService.java` | Service layer | Main app-facing prediction API |
| `src/main/java/com/tracker/academictracker/mapper/PredictionMapper.java` | Mapper layer | Converts app models into prediction-core DTOs |
| `src/main/java/com/academictracker/prediction2/facade/PredictionCoreFacade.java` | Facade | Simplifies access to multiple prediction engines |
| `src/main/java/com/academictracker/prediction2/engine/*.java` | Prediction engines | Perform course-grade, target-grade, GPA, and target-GPA calculations |
| `src/main/java/com/tracker/academictracker/ui/controller/DashboardController.java` | Dashboard controller | Uses prediction service for GPA projection and target GPA |
| `src/main/java/com/tracker/academictracker/ui/controller/CourseDetailsController.java` | Course details controller | Uses prediction service for course-grade and course-target prediction |
| `src/main/java/com/tracker/academictracker/persistence/DataManager.java` | Persistence | Saves and loads `student_data.json` |
| `student_data.json` | Data file | Stores user data without changing format |

## 9. Conclusion

The prediction-core integration is a logical improvement for an OOP Java project. It keeps business calculation separate from JavaFX UI code, preserves JSON compatibility, and introduces a cleaner architecture through service, mapper, facade, and engine layers. The result is easier to maintain, easier to test, and easier to explain academically.
