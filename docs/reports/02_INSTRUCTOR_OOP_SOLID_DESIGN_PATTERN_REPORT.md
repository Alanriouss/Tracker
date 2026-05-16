# 02 — Instructor Report: OOP, SOLID, Design Patterns, and Architecture

## 1. Introduction

Academic Tracker is a Java 21 and JavaFX desktop application for managing student academic data, including semesters, courses, assessments, scores, projected GPA, target GPA, and course-grade prediction. It is a suitable OOP in Java project because it combines object modeling, event-driven UI, JSON persistence, and separated business logic.

Architecturally, the project does not place all logic inside the UI layer. Instead, it separates concerns into domain model, controller, persistence, service, mapper, and prediction-core packages. This makes the code easier to maintain, extend, and explain from an OOP perspective.

## 2. OOP Concepts Applied in the Project

### 2.1 Class and Object

Definition:

- A `class` is a blueprint that defines state and behavior.
- An `object` is a concrete instance created from a class.

Where it appears:

- `Student`, `Semester`, `Course`, and `Assessment` in `src/main/java/com/tracker/academictracker/domain/model/`

Examples:

- A `Student` object contains a list of `Semester` objects.
- A `Course` object contains a list of `Assessment` objects.

Why useful here:

- The academic domain maps naturally to real-world objects.

Trade-off:

- More relationships between objects can increase conceptual complexity for beginners.

### 2.2 Encapsulation

Definition:

- Encapsulation means grouping data and behavior inside a class while controlling access through methods.

Where it appears:

- Model fields are `private`.
- Access is controlled through getters/setters.
- `Assessment.setWeight(...)`, `Assessment.setMaxScore(...)`, and `Assessment.setScore(...)` validate input.

Why useful here:

- It protects object state from invalid values.
- It makes the code safer and more predictable.

Trade-off:

- It introduces more getter/setter methods.

### 2.3 Abstraction

Definition:

- Abstraction hides complex details behind a simpler interface.

Where it appears:

- Controllers call `PredictionService` instead of low-level engines.
- `PredictionService` hides the mapper/facade workflow.
- `PredictionCoreFacade` hides the internal prediction engines.

Why useful here:

- The UI layer stays readable and does not need to know core calculation details.

Trade-off:

- There are more layers to understand.

### 2.4 Inheritance

Where it appears:

- `Main extends Application` from JavaFX.

Honest evaluation:

- The project does not build a large custom inheritance hierarchy in the domain model.
- That is not a weakness by itself, because this domain is better represented with composition than inheritance.

Why that is acceptable:

- Not every OOP project needs deep subclass trees.
- Forced inheritance would make the model less natural here.

### 2.5 Polymorphism

Where it appears:

- Mostly through framework usage and JavaFX extension points.
- Examples include anonymous `TableCell<>` subclasses in controller table setups.

Why useful here:

- The project still benefits from polymorphic framework behavior without forcing unnecessary domain inheritance.

Trade-off:

- Polymorphism is not the most visible OOP feature in this codebase.

### 2.6 Composition

This is the strongest OOP relationship in the project:

```text
Student has Semesters
Semester has Courses
Course has Assessments
```

Why useful here:

- The academic domain is a clear `has-a` structure.

Trade-off:

- The root object graph becomes larger, especially for persistence and UI updates.

### 2.7 Association / Aggregation / Composition

UML-like view:

```text
Student *-- Semester
Semester *-- Course
Course *-- Assessment
Course --> CourseStatus
```

Explanation:

- `Student -> Semester`, `Semester -> Course`, and `Course -> Assessment` are best explained as composition in this project.
- `Course -> CourseStatus` is an association to an enum representing state.

## 3. SOLID Principles Applied in the Project

### 3.1 S — Single Responsibility Principle

Definition:

- Each class should have one main reason to change.

Where it appears:

- `PredictionMapper` only maps data.
- `PredictionService` only coordinates prediction use cases.
- `DataManager` handles persistence.
- `SmoothScrollUtil` handles scroll behavior.
- Domain model classes represent academic data.

Benefit:

- The code is easier to change and explain.

Weakness:

- Some controllers are still large and carry multiple UI responsibilities.

### 3.2 O — Open/Closed Principle

Definition:

- Software should be open for extension but closed for unnecessary modification.

Where it appears:

- prediction-core can grow internally while controllers continue calling `PredictionService`.
- New prediction use cases can be added to the service/facade flow.

Benefit:

- The UI does not need to know every internal engine detail.

Weakness:

- Adding a new screen still requires controller/FXML work.

### 3.3 L — Liskov Substitution Principle

Definition:

- Subtypes should be substitutable for their base types without breaking behavior.

Honest evaluation:

- LSP is not strongly demonstrated because the project does not rely on a deep custom inheritance hierarchy.

Why this is acceptable:

- The project favors composition and service delegation instead of forced inheritance.

### 3.4 I — Interface Segregation Principle

Definition:

- Clients should not be forced to depend on interfaces they do not use.

Honest evaluation:

- The project has few custom interfaces, so ISP is not strongly demonstrated.
- At the same time, the code avoids oversized custom interfaces.

Future improvement:

- Introduce interfaces for service or persistence only if testing needs grow.

### 3.5 D — Dependency Inversion Principle

Definition:

- High-level modules should not depend directly on low-level details.

Where it appears conceptually:

- Controllers depend on `PredictionService` rather than direct engine calls.
- `PredictionService` shields the UI from prediction-core internals.

Weakness:

- Controllers still instantiate concrete services directly with `new PredictionService()`.
- `DataManager` is static.

Future improvement:

- Add dependency injection if the app grows further.

## 4. Design Patterns Used or Implied

### 4.1 MVC Pattern

Where:

- View = FXML files
- Controller = JavaFX controller classes
- Model = domain model classes

Problem solved:

- Separates layout from event-handling logic and domain data.

Why it fits:

- JavaFX works naturally with FXML + controller + model.

Trade-off:

- Refactoring controller packages requires updating `fx:controller` paths carefully.

### 4.2 Service Layer Pattern

Where:

- `PredictionService`

Problem solved:

- Keeps use-case logic out of controllers.

Why it fits:

- The UI needs high-level operations such as course-grade prediction and target GPA calculation.

Trade-off:

- One more abstraction layer to learn.

### 4.3 Mapper / Adapter Pattern

Where:

- `PredictionMapper`

Problem solved:

- Converts app domain objects into prediction-core DTOs.

Why it fits:

- The app model is tied to UI and JSON persistence.
- prediction-core needs clean report-friendly inputs.

Trade-off:

- Mapping must stay accurate, especially score normalization.

### 4.4 Facade Pattern

Where:

- `PredictionCoreFacade`

Problem solved:

- Provides one simple API over multiple engines.

Why it fits:

- The app should not construct and coordinate all engines directly.

Trade-off:

- The facade must be updated when new prediction use cases are added.

### 4.5 Repository / Persistence-like Pattern

Where:

- `DataManager`

Problem solved:

- Centralizes save/load JSON logic.

Why it fits:

- Domain models should not do file I/O directly.

Trade-off:

- It is not a full repository abstraction yet; it is a simple static persistence utility.

### 4.6 Utility Pattern

Where:

- `SmoothScrollUtil`

Problem solved:

- Reuses scrolling behavior across screens.

Why it fits:

- Smooth scrolling is shared UI logic, not business logic.

Trade-off:

- Static utilities are best for small reusable behavior, not for every concern.

## 5. Architecture Overview

Prediction flow:

```text
UI / FXML
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

This layered design is logical because it separates screen behavior, application-level use cases, data conversion, business calculation, and persistence.

## 6. Why This Architecture Fits an OOP Java Course

- It demonstrates object modeling clearly.
- It separates UI from business calculation.
- It uses composition naturally.
- It applies useful patterns without overengineering.
- It is realistic enough to discuss architecture, maintainability, and SOLID.

## 7. Trade-offs and Limitations

- More structure means more files.
- More abstraction can be harder for beginners at first.
- Some controllers are still large.
- LSP and ISP are only partially demonstrated.
- JavaFX UI testing is harder than testing pure prediction-core logic.

## 8. Future Improvements

- Add unit tests for `PredictionMapper` and `PredictionService`.
- Introduce dependency injection if the project grows.
- Improve controller size by extracting more UI helpers.
- Add better UI validation feedback.
- Replace JSON with a database only if the project scope grows.

## 9. Conclusion

Academic Tracker is a strong OOP course project because it combines a natural object model, practical UI interaction, persistence, and a separated prediction engine. The architecture is not perfect, but it is logically organized, maintainable, and easy to discuss in terms of OOP, SOLID, and design patterns.
