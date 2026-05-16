# 01 — Team Code Explanation Report

## 1. App Overview

Academic Tracker is a JavaFX desktop app for managing a student's academic information. The main users are students who want to store semesters, courses, assessments, and scores, then view GPA-related summaries and prediction features.

Verified main features in the current codebase:

- Create a student profile in `Welcome.fxml` through `WelcomeController`.
- Load and save data through `student_data.json` using `DataManager`.
- Manage `Semester`, `Course`, and `Assessment` objects.
- Show a `Dashboard` with term GPA, overall GPA, and active-course widgets.
- Calculate target GPA in `DashboardController`.
- Calculate current course grade and target course grade in `CourseDetailsController`.
- Import, export, save, and load data in `SettingsController`.
- Improve scrolling behavior with `SmoothScrollUtil` and custom CSS.

Verified technologies:

- Java 21
- JavaFX
- Maven and Maven Wrapper (`mvnw`, `mvnw.cmd`)
- Gson for JSON
- Integrated prediction-core in `com.academictracker.prediction2`

prediction-core acts as the business logic layer for prediction features. Controllers do not call the engines directly. They go through `PredictionService` and `PredictionMapper`, then into `PredictionCoreFacade`.

## 2. High-Level Folder Structure

Current verified structure:

```text
academic-tracker-master-2
├── docs/
│   ├── final-deliverables/
│   └── reports/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/tracker/academictracker/
│   │   │   │   ├── app/
│   │   │   │   ├── domain/model/
│   │   │   │   ├── mapper/
│   │   │   │   ├── persistence/
│   │   │   │   ├── service/
│   │   │   │   └── ui/
│   │   │   │       ├── controller/
│   │   │   │       └── util/
│   │   │   └── com/academictracker/prediction2/
│   │   └── resources/com/tracker/academictracker/
│   └── test/java/com/academictracker/prediction2/
├── student_data.json
├── pom.xml
├── mvnw
└── mvnw.cmd
```

### `app/`

Contains:

- `Launcher.java`
- `Main.java`

Responsibility:

- Start the JavaFX application.
- Load the first screen.
- Decide whether to show `Welcome` or `Dashboard`.

Why separate it:

- Entry-point logic is different from domain logic and UI controller logic.

If not separated:

- Startup code would mix with UI or business logic, making the project harder to explain.

### `domain/model/`

Contains:

- `Student`
- `Semester`
- `Course`
- `Assessment`
- `CourseStatus`

Responsibility:

- Represent the academic data model of the app.

Why separate it:

- This package is the center of the academic domain.
- It supports OOP concepts like class, object, composition, and encapsulation.

If not separated:

- Data structure and UI logic would become tangled.

### `ui/controller/`

Contains:

- `WelcomeController`
- `DashboardController`
- `SemestersController`
- `CourseRosterController`
- `CourseDetailsController`
- `SettingsController`

Responsibility:

- Handle JavaFX UI events.
- Update labels, tables, and forms.
- Coordinate between UI and services/persistence.

Why separate it:

- Controllers should focus on UI behavior, not data conversion or core calculation rules.

If not separated:

- Business logic would spread across screens and become difficult to test or maintain.

### `service/`

Contains:

- `PredictionService`

Responsibility:

- Provide app-level prediction use cases to controllers.

Why separate it:

- Controllers should not call low-level prediction engines directly.

If not separated:

- Every controller would need to know too much about prediction-core internals.

### `mapper/`

Contains:

- `PredictionMapper`

Responsibility:

- Convert app domain models into prediction-core DTOs.

Why separate it:

- App models and prediction-core models have different roles.

If not separated:

- Tight coupling would increase and score normalization could be forgotten or duplicated.

### `persistence/`

Contains:

- `DataManager`

Responsibility:

- Save and load student data from JSON.

Why separate it:

- File I/O is infrastructure, not UI logic and not core domain logic.

If not separated:

- Models or controllers would become responsible for storage details.

### `ui/util/`

Contains:

- `SmoothScrollUtil`

Responsibility:

- Reusable JavaFX UI helper behavior.

Why separate it:

- Shared UI behavior should not be copied into multiple controllers.

### `resources/FXML`

Contains:

- `Welcome.fxml`
- `Dashboard.fxml`
- `Semesters.fxml`
- `CourseRoster.fxml`
- `CourseDetails.fxml`
- `Settings.fxml`

Responsibility:

- Define the visual structure of screens.

### `resources/CSS`

Contains:

- `style.css`

Responsibility:

- Define common styling for tables, scrollbars, and visual appearance.

### `com.academictracker.prediction2`

Contains:

- `engine`
- `exception`
- `facade`
- `model`
- `policy`
- `result`

Responsibility:

- Hold the independent business logic layer for prediction features.

Why separate it:

- prediction-core should stay independent from JavaFX, FXML, Gson, and controllers.

## 3. Main App Flow

### Startup flow

```text
User runs app
  ↓
Launcher.main()
  ↓
Main.start(Stage)
  ↓
DataManager.saveFileExists()
  ↓
Load student_data.json if it exists
  ↓
Show Welcome or Dashboard
```

### View loading flow

```text
Main / Controller
  ↓
FXMLLoader
  ↓
FXML file
  ↓
Controller instance
  ↓
initData(...)
```

### Dashboard navigation flow

```text
User clicks sidebar button
  ↓
DashboardController.handleNavigation(...)
  ↓
FXMLLoader loads a new screen
  ↓
Controller receives student/semester context
  ↓
View is placed in mainContentPane
```

### Prediction flow

```text
User action
  ↓
Controller
  ↓
PredictionService
  ↓
PredictionMapper
  ↓
PredictionCoreFacade
  ↓
Prediction engine
  ↓
Result object
  ↓
Controller formats UI text
```

### Persistence flow

```text
User changes data
  ↓
Controller
  ↓
DataManager.saveStudent(...)
  ↓
student_data.json
```

## 4. Main Class Groups

### 4.1 App entry point

#### `src/main/java/com/tracker/academictracker/app/Launcher.java`

Responsibility:

- Minimal launcher for JavaFX startup.

Important method:

- `main(String[] args)`

Why it exists:

- Keeps the actual `Application` class separate from the thin entry point.

#### `src/main/java/com/tracker/academictracker/app/Main.java`

Responsibility:

- Start JavaFX.
- Load `Welcome.fxml` or `Dashboard.fxml`.
- Apply smooth scrolling to loaded roots.

Important methods:

- `start(Stage stage)`
- `showWelcomeScreen()`
- `showDashboard(Student student)`
- `main(String[] args)`

Why it exists:

- It centralizes application startup and first-screen decisions.

### 4.2 Domain model

#### `Student`

Responsibility:

- Represent one student and own the list of semesters.

Important fields:

- `fullName`
- `studentId`
- `semesters`
- `activeSemesterName`

Important methods:

- `addSemester(...)`
- `removeSemester(...)`
- `getCumulativeGPA()`
- `getCompletedGPA()`
- `getTotalCreditsCompleted()`
- `getAcademicStanding()`

Benefits:

- Clear root object for persistence and app state.

Limitations:

- Some GPA helper methods are legacy and partly overlap with prediction-core concepts.

#### `Semester`

Responsibility:

- Represent one academic term and own the list of courses.

Important fields:

- `year`
- `termType`
- `courses`

Important methods:

- `addCourse(...)`
- `removeCourse(...)`
- `getDisplayName()`
- `getGPA()`
- `getAverageScore()`

#### `Course`

Responsibility:

- Represent one course and own the list of assessments.

Important fields:

- `courseCode`
- `courseName`
- `credits`
- `status`
- `assessments`

Important methods:

- `addAssessment(...)`
- `removeAssessment(...)`
- `getTotalWeight()`
- `isWeightTotalValid()`
- `getCurrentGrade()`

#### `Assessment`

Responsibility:

- Represent one assessment component.

Important fields:

- `assessmentName`
- `category`
- `weight`
- `score`
- `maxScore`

Important methods:

- `isGraded()`
- `getWeightScore()`

Why this class matters:

- It holds the raw score/max score pair that later must be normalized for prediction-core.

#### `CourseStatus`

Responsibility:

- Represent the lifecycle state of a course.

Values:

- `ACTIVE`
- `COMPLETED`
- `DROPPED`

### 4.3 Persistence

#### `src/main/java/com/tracker/academictracker/persistence/DataManager.java`

Responsibility:

- Save and load `Student` data as JSON.

Important methods:

- `saveStudent(Student student)`
- `loadStudent()`
- `saveFileExists()`

Why this class exists:

- It isolates JSON and file operations from controllers and domain models.

Benefits:

- Easier to change persistence later.

Limitations:

- Static utility design is simple but less flexible than full dependency injection.

### 4.4 Controllers

#### `WelcomeController`

Responsibility:

- Create the initial student profile.

#### `DashboardController`

Responsibility:

- Show overall dashboard data.
- Handle target GPA prediction.
- Navigate between screens.

Important prediction methods:

- `updateOverallGpaLabel()`
- `updateTermWidgets(...)`
- `calculateTargetGrades()`

#### `CourseDetailsController`

Responsibility:

- Show assessment list for a course.
- Calculate current course grade.
- Predict the target grade needed for remaining assessments.

Important prediction methods:

- `refreshData()`
- `calculateCourseTarget()`

#### `CourseRosterController`

Responsibility:

- Show course list.
- Add/delete courses.
- Open course details.

#### `SemestersController`

Responsibility:

- Add and list semesters.
- Set the active semester.

#### `SettingsController`

Responsibility:

- Save, load, import, and export data.

### 4.5 Prediction integration

#### `src/main/java/com/tracker/academictracker/service/PredictionService.java`

Responsibility:

- Offer prediction use cases to the UI.

Important methods:

- `calculateCourseGrade(...)`
- `predictCourseTarget(...)`
- `projectStudentGpa(...)`
- `projectSemesterGpa(...)`
- `calculateRequiredActiveTermGpa(...)`

#### `src/main/java/com/tracker/academictracker/mapper/PredictionMapper.java`

Responsibility:

- Convert app models into prediction-core DTOs.

Important methods:

- `toTranscriptInput(...)`
- `toSemesterInput(...)`
- `toCourseInput(...)`
- `toAssessmentInput(...)`
- `countActiveCredits(...)`

#### `src/main/java/com/academictracker/prediction2/facade/PredictionCoreFacade.java`

Responsibility:

- Provide a simple public API over the prediction engines.

Important methods:

- `calculateCourseGrade(...)`
- `predictCourseTarget(...)`
- `projectGpa(...)`
- `calculateRequiredFutureGpa(...)`

#### Prediction-core model/result/policy/engine

Important groups:

- `model`: input DTOs
- `result`: output records and statuses
- `policy`: validation and grade-scale rules
- `engine`: actual calculations
- `exception`: invalid-input exception type

### 4.6 UI utilities

#### `src/main/java/com/tracker/academictracker/ui/util/SmoothScrollUtil.java`

Responsibility:

- Apply smooth scrolling behavior to `ScrollPane` nodes.

Important methods:

- `applyTo(Node root)`
- `applyTo(ScrollPane scrollPane)`

Why it exists:

- Shared UI behavior should not be rewritten in every controller.

## 5. Why Not Put All Calculations in Controllers?

Controllers should mainly coordinate UI. If they also own heavy business calculations:

- The UI layer becomes harder to read.
- Testing becomes harder because JavaFX controllers are more awkward to unit test.
- The same formulas may be duplicated across screens.
- A change in prediction rules may force multiple UI edits.

By moving prediction logic into `PredictionService` and prediction-core:

- Controllers stay focused on event handling and display.
- The calculation layer becomes easier to test.
- The design is easier to explain in an OOP course.

Trade-off:

- There are more classes and one extra abstraction layer to understand.

## 6. How prediction-core Works in the App

### Data the app already has

- `Student`
- `Semester`
- `Course`
- `Assessment`
- `CourseStatus`

### Why a mapper is needed

The app stores domain objects designed for UI and JSON persistence. prediction-core expects small, clean input DTOs designed only for calculation. The mapper bridges these two representations.

### Why scores must be normalized to 0-100

The app stores:

```text
score = raw achieved points
maxScore = maximum possible points
```

prediction-core expects:

```text
score = percent on a 0-100 scale
```

Example:

```text
score = 8
maxScore = 10
normalized = 8 / 10 * 100 = 80
```

If this conversion is skipped, prediction-core would treat `8/10` as `8%` instead of `80%`, which would break course and GPA calculations.

### Prediction: course grade

`CourseDetailsController.refreshData()` calls:

```text
PredictionService.calculateCourseGrade(currentCourse)
```

The result comes back as a `GradeResult`.

### Prediction: course target

`CourseDetailsController.calculateCourseTarget()` calls:

```text
PredictionService.predictCourseTarget(currentCourse, targetGrade)
```

The result comes back as a `CoursePredictionResult`.

### Prediction: GPA

`DashboardController.updateOverallGpaLabel()` calls:

```text
PredictionService.projectStudentGpa(student)
```

`DashboardController.updateTermWidgets(...)` calls:

```text
PredictionService.projectSemesterGpa(activeSemester)
```

### Prediction: target GPA

`DashboardController.calculateTargetGrades()` calls:

```text
PredictionService.calculateRequiredActiveTermGpa(student, activeSem, targetGpa)
```

The result comes back as a `TargetGpaResult`.

### Existing validation

Validation happens in two main places:

- App model validation, especially in `Assessment`
- prediction-core validation through `ValidationPolicy`

Invalid cases include:

- total assessment weight over 100%
- invalid scores
- invalid target GPA
- missing remaining credits
- missing active credits for target GPA calculation

## 7. Smooth Scrolling / UX Improvement

The app now uses `SmoothScrollUtil` to apply a smoother scroll effect to `ScrollPane` nodes. It is placed in `ui/util` because scrolling behavior is reusable UI logic, not business logic and not screen-specific controller logic.

Benefits:

- Better user experience on long pages
- Less duplicated scroll setup code
- Easier reuse across multiple screens

Limitations:

- It still depends on JavaFX event behavior and visual tuning
- Any change to scroll feeling may require UI testing across screens

## 8. Benefits of the Current Structure

- Better maintainability
- Better readability
- Better separation of concerns
- Easier extension of prediction features
- Easier explanation to an instructor
- Better testability for prediction logic
- Clearer mapping between design patterns and code structure

## 9. Drawbacks / Trade-offs of the Current Structure

- More classes and packages
- More imports
- Mapper layer adds one more concept for beginners
- Controllers can still become large because JavaFX UI logic grows quickly
- Package refactors can break FXML if controller paths are not updated
- JavaFX + Maven setup may be unfamiliar to classmates

## 10. Key Presentation Points for the Team

- The app models academic data with composition: `Student -> Semester -> Course -> Assessment`.
- Controllers handle UI events, not heavy prediction formulas.
- `PredictionService` is the service layer between UI and prediction-core.
- `PredictionMapper` is the adapter between app models and core DTOs.
- `PredictionCoreFacade` hides multiple engines behind one API.
- `DataManager` handles JSON persistence.
- `student_data.json` format remains unchanged.
- prediction-core is fully inside the app project and does not require an external folder.

## 11. Glossary

- `JavaFX`: Java UI toolkit for desktop applications.
- `FXML`: XML-based layout description for JavaFX views.
- `Controller`: Java class that responds to UI events and updates the view.
- `Model`: Data classes that represent the academic domain.
- `Service`: Application-level class that coordinates a use case.
- `Mapper`: Class that converts one object model into another.
- `DTO`: Data Transfer Object, a simple object used to pass structured data.
- `Facade`: A simplified API over multiple internal components.
- `Persistence`: Saving and loading data from storage.
- `JSON`: Text-based format used here to store student data.
- `Maven`: Build and dependency management tool.
- `Prediction engine`: Class that performs a specific calculation.
- `Validation`: Rules that reject invalid input before or during processing.
- `Encapsulation`: Keeping fields controlled through class methods.
- `Abstraction`: Hiding complex details behind simpler APIs.
- `Inheritance`: Reusing behavior through class extension.
- `Polymorphism`: Using a common type/interface with different concrete behavior.
