# 02 — Full App UML Documentation

This document explains the UML view of the Academic Tracker app based on the current code in `academic-tracker-master-2`.

## 1. Package Diagram

Verified main packages:

```text
com.tracker.academictracker.app
com.tracker.academictracker.domain.model
com.tracker.academictracker.persistence
com.tracker.academictracker.service
com.tracker.academictracker.mapper
com.tracker.academictracker.ui.controller
com.tracker.academictracker.ui.util
com.academictracker.prediction2
```

What each package does:

- `app`: JavaFX startup and entry point
- `domain.model`: academic domain objects
- `persistence`: JSON save/load
- `service`: app-level prediction use cases
- `mapper`: conversion between app model and prediction-core DTOs
- `ui.controller`: JavaFX screen controllers
- `ui.util`: reusable UI helper logic
- `com.academictracker.prediction2`: prediction-core business logic

Why this separation supports clean architecture:

- UI and persistence are separated from business logic.
- prediction-core stays independent from JavaFX and Gson.
- Service and mapper layers make prediction integration easier to reason about.

Related UML source:

- `docs/final-deliverables/uml/package-diagram.md`

## 2. Class Diagram — Domain Model

Main classes:

- `Student`
- `Semester`
- `Course`
- `Assessment`
- `CourseStatus`

Important relationships:

- `Student` has many `Semester`
- `Semester` has many `Course`
- `Course` has many `Assessment`
- `Course` has one `CourseStatus`

Why composition matters here:

- The academic domain is naturally a `has-a` structure.
- Composition is more appropriate than inheritance for these objects.

Verified important methods:

- `Student`: `addSemester`, `removeSemester`, `getCumulativeGPA`, `getCompletedGPA`, `getAcademicStanding`
- `Semester`: `addCourse`, `removeCourse`, `getDisplayName`, `getGPA`
- `Course`: `addAssessment`, `removeAssessment`, `getTotalWeight`, `getCurrentGrade`
- `Assessment`: `isGraded`, `getWeightScore`

Related UML source:

- `docs/final-deliverables/uml/class-diagram.puml`

## 3. Class Diagram — Prediction Integration

Main classes involved:

- `DashboardController`
- `CourseDetailsController`
- `PredictionService`
- `PredictionMapper`
- `PredictionCoreFacade`
- `CourseGradeEngine`
- `CoursePredictionEngine`
- `GpaProjectionEngine`
- `TargetGpaEngine`
- DTOs: `TranscriptInput`, `SemesterInput`, `CourseInput`, `AssessmentInput`
- Results: `GradeResult`, `CoursePredictionResult`, `GpaProjectionResult`, `TargetGpaResult`

Dependencies:

- Controllers depend on `PredictionService`
- `PredictionService` depends on `PredictionMapper` and `PredictionCoreFacade`
- `PredictionMapper` converts app domain models into prediction-core DTOs
- `PredictionCoreFacade` hides the internal engine structure

This is the clearest example of layered architecture in the project.

## 4. Class Diagram — Persistence

Main persistence elements:

- `DataManager`
- `Student`
- external file `student_data.json`

Explanation:

- `DataManager` performs save/load operations.
- `student_data.json` is the persistence artifact.
- Domain model classes do not perform direct file I/O themselves.

## 5. MVC Diagram

In this project:

- View = FXML files
- Controller = JavaFX controller classes
- Model = domain model classes

Why this matters:

- FXML keeps layout separate from Java event-handling code.
- Controllers coordinate user actions and screen updates.
- Models represent academic data and related helper behavior.

## 6. Design Pattern Diagram

### MVC Pattern

Where it appears:

- FXML views
- controller classes
- domain model classes

Problem it solves:

- Separates screen layout, event handling, and data modeling.

### Service Layer Pattern

Where it appears:

- `PredictionService`

Problem it solves:

- Gives controllers a high-level prediction API.

### Mapper / Adapter Pattern

Where it appears:

- `PredictionMapper`

Problem it solves:

- Bridges app models and prediction-core DTOs.

### Facade Pattern

Where it appears:

- `PredictionCoreFacade`

Problem it solves:

- Hides multiple engine classes behind one entry point.

### Persistence / Repository-like Pattern

Where it appears:

- `DataManager`

Problem it solves:

- Centralizes JSON persistence.

### Utility Pattern

Where it appears:

- `SmoothScrollUtil`

Problem it solves:

- Reuses scrolling behavior across screens.

## 7. Sequence Diagram — App Startup

Flow:

```text
User runs app
  ↓
Launcher.main()
  ↓
Application.launch(Main.class, args)
  ↓
Main.start(Stage)
  ↓
DataManager.saveFileExists()
  ↓
DataManager.loadStudent() if save exists
  ↓
FXMLLoader loads Welcome.fxml or Dashboard.fxml
  ↓
Controller initialized
```

## 8. Sequence Diagram — Target GPA Calculation

Flow:

```text
User enters target GPA
  ↓
DashboardController.calculateTargetGrades()
  ↓
PredictionService.calculateRequiredActiveTermGpa()
  ↓
PredictionMapper.countActiveCredits()
  ↓
PredictionMapper.toTranscriptInput()
  ↓
PredictionCoreFacade.calculateRequiredFutureGpa()
  ↓
TargetGpaEngine.calculateRequiredFutureGpa()
  ↓
TargetGpaResult
  ↓
DashboardController updates UI
```

Related UML source:

- `docs/final-deliverables/uml/sequence-target-gpa.md`

## 9. Sequence Diagram — Course Target Grade Prediction

Flow:

```text
User enters target course grade
  ↓
CourseDetailsController.calculateCourseTarget()
  ↓
PredictionService.predictCourseTarget()
  ↓
PredictionMapper.toCourseInput()
  ↓
PredictionCoreFacade.predictCourseTarget()
  ↓
CoursePredictionEngine.predictTarget()
  ↓
CoursePredictionResult
  ↓
CourseDetailsController updates UI
```

Related UML source:

- `docs/final-deliverables/uml/sequence-course-prediction.md`

## 10. Sequence Diagram — Save/Load Data

Load flow:

```text
App opens
  ↓
DataManager.loadStudent()
  ↓
student_data.json
  ↓
Student object
```

Save flow:

```text
User changes data
  ↓
Controller
  ↓
DataManager.saveStudent()
  ↓
student_data.json
```

## 11. SOLID Explanation Using UML

### SRP — Single Responsibility Principle

Visible in:

- `PredictionMapper`
- `PredictionService`
- `DataManager`
- prediction engines
- `SmoothScrollUtil`

Weak point:

- Some controllers still carry too many UI-related responsibilities.

### OCP — Open/Closed Principle

Visible in:

- prediction-core can grow internally while app controllers keep calling service/facade APIs.

### LSP — Liskov Substitution Principle

Honest evaluation:

- Not strongly demonstrated because the project does not rely on a large custom inheritance hierarchy.

### ISP — Interface Segregation Principle

Honest evaluation:

- Not strongly demonstrated because the project has few custom interfaces.
- This is acceptable for the current scale.

### DIP — Dependency Inversion Principle

Visible in:

- Controllers depend on service-level prediction access instead of low-level engine calls.

Weak point:

- Concrete classes are still instantiated directly rather than injected.

## 12. Instructor Explanation Script

### 1-minute architecture explanation

"The app is separated by responsibility. FXML defines the screens, controllers handle JavaFX events, domain models represent academic data, `DataManager` handles JSON persistence, and prediction features flow through `PredictionService`, `PredictionMapper`, and `PredictionCoreFacade` into prediction-core."

### 1-minute OOP model explanation

"The domain model is built around composition. A `Student` contains many `Semester` objects, each `Semester` contains many `Course` objects, and each `Course` contains many `Assessment` objects. This models the academic domain naturally and demonstrates encapsulation and object relationships clearly."

### 1-minute prediction-core integration explanation

"Controllers do not calculate target GPA or target course grades directly. They call `PredictionService`, which converts app data with `PredictionMapper` and delegates to `PredictionCoreFacade`. The core returns structured result objects that the controller formats for the UI."

### 1-minute design pattern explanation

"The app uses MVC for view-controller-model separation, Service Layer for app-level prediction use cases, Mapper/Adapter for data conversion, Facade for prediction-core access, a persistence-like layer in `DataManager`, and a utility pattern in `SmoothScrollUtil`."
