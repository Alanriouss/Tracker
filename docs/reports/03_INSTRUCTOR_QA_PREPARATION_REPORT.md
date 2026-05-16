# 03 — Instructor Q&A Preparation Report

## 1. Questions about App Purpose

### Q1. What problem does this app solve?
A1. It helps students store semesters, courses, assessments, and scores in a structured way, then calculate or predict course grades, projected GPA, and target GPA.

### Q2. Why choose Academic Tracker for an OOP project?
A2. The domain model is very clear: `Student`, `Semester`, `Course`, and `Assessment`. That makes it easy to explain classes, objects, composition, encapsulation, and layered architecture.

### Q3. Who is the main user?
A3. The main user is a student who wants to manage academic progress and estimate future performance targets.

### Q4. What are the main features?
A4. Profile creation, JSON save/load, semester management, course management, assessment tracking, dashboard summaries, current grade calculation, target course grade prediction, projected GPA, and target GPA calculation.

## 2. Questions about OOP Concepts

### Q1. What is the difference between a class and an object in this project?
A1. `Student`, `Semester`, `Course`, and `Assessment` are classes. When the app creates `new Student(name, studentId)` in `WelcomeController`, that is an object instance.

### Q2. Give a real object example from the code.
A2. `WelcomeController.handleCreateProfile(...)` creates a `Student` object using the student's name and ID.

### Q3. Where is encapsulation applied?
A3. Model fields are private and are accessed through getters/setters. `Assessment` also validates `weight`, `maxScore`, and `score` through its setter methods.

### Q4. Why is encapsulation useful here?
A4. It prevents invalid state, such as negative weights or a score greater than `maxScore`.

### Q5. How is abstraction shown in this app?
A5. Controllers do not know the full prediction-engine flow. They call `PredictionService`, which then coordinates `PredictionMapper` and `PredictionCoreFacade`.

### Q6. Does this project use inheritance?
A6. Yes, but not heavily. The main clear example is `Main extends Application` from JavaFX.

### Q7. Why is inheritance not used much?
A7. This domain is better modeled with composition. A `Student` is not a kind of `Semester`; a `Student` contains many `Semester` objects.

### Q8. Is the project still object-oriented without many subclasses?
A8. Yes. OOP is not only about inheritance. This project shows OOP strongly through object modeling, composition, encapsulation, and abstraction.

### Q9. Where does polymorphism appear?
A9. Mostly through JavaFX framework behavior, including controller interactions and custom `TableCell<>` implementations in table views.

### Q10. What is the most important composition relationship in the app?
A10. `Student` has `Semester`, `Semester` has `Course`, and `Course` has `Assessment`.

### Q11. Why is composition better than inheritance for `Student-Semester-Course-Assessment`?
A11. Because the relationship is `has-a`, not `is-a`.

### Q12. How would you describe association, aggregation, and composition here?
A12. In this project, the main chain is best explained as composition through object ownership inside lists.

### Q13. Why is `CourseStatus` useful in OOP terms?
A13. It replaces loose strings with a clear, type-safe enum that expresses course state.

### Q14. How does JavaFX event handling relate to OOP?
A14. Controllers are objects that receive and respond to UI events. Buttons trigger controller methods like `handleCreateProfile`, `handleNavigation`, and `calculateCourseTarget`.

### Q15. Do model classes contain behavior or only data?
A15. They contain both. For example, `Student`, `Semester`, and `Course` have helper methods such as GPA or grade-related methods in addition to stored fields.

## 3. Questions about SOLID

### Q1. What is SRP?
A1. SRP means a class should have one main reason to change.

### Q2. Where is SRP applied?
A2. `PredictionMapper` maps data, `PredictionService` coordinates prediction use cases, `DataManager` handles persistence, and `SmoothScrollUtil` handles scroll behavior.

### Q3. Which class shows SRP most clearly?
A3. `PredictionMapper` is one of the clearest examples because it focuses only on conversion between app models and prediction DTOs.

### Q4. Do controllers violate SRP?
A4. Partly. They mainly handle UI, but some of them are still large and contain navigation, table setup, and persistence calls.

### Q5. What is OCP?
A5. OCP means software should be open for extension but closed for unnecessary modification.

### Q6. How does this app follow OCP?
A6. New prediction logic can be added inside prediction-core and exposed through `PredictionCoreFacade` and `PredictionService` without rewriting the whole UI layer.

### Q7. Is LSP strongly demonstrated?
A7. No. The project does not rely on a deep inheritance hierarchy.

### Q8. Is that a weakness?
A8. Not necessarily. This domain is better represented with composition than forced inheritance.

### Q9. Is ISP strongly demonstrated?
A9. Not strongly, because the project has few custom interfaces. However, it also avoids large unnecessary interfaces.

### Q10. How is DIP shown?
A10. Controllers depend conceptually on the service layer instead of direct engine classes.

### Q11. Where is DIP still incomplete?
A11. Controllers still instantiate concrete services directly, and `DataManager` is static.

### Q12. Which principle is strongest in the project?
A12. SRP is one of the strongest because the mapper, service, facade, engines, and persistence utility all have clear roles.

### Q13. Which principle is weakest?
A13. LSP and ISP are the weakest because the code does not emphasize inheritance or custom interfaces.

### Q14. Why is `DataManager` separated?
A14. To keep persistence concerns out of the UI and domain model.

### Q15. Why does `PredictionService` exist?
A15. To give controllers a clean API for prediction use cases without exposing low-level prediction-engine details.

## 4. Questions about Design Patterns

### Q1. What is MVC in this app?
A1. FXML files are the View, controller classes are the Controller, and domain model classes are the Model.

### Q2. Why does MVC fit JavaFX?
A2. JavaFX supports FXML plus controllers naturally, which makes it easy to separate screen layout from event-handling code.

### Q3. Where is the Service Layer pattern used?
A3. In `PredictionService`.

### Q4. Why not write prediction logic directly in controllers?
A4. That would make UI code harder to maintain, harder to test, and harder to reuse.

### Q5. Where is the Mapper/Adapter pattern used?
A5. In `PredictionMapper`.

### Q6. Why is the mapper necessary?
A6. Because app domain models and prediction-core DTOs have different responsibilities and data formats.

### Q7. Where is the Facade pattern used?
A7. In `PredictionCoreFacade`.

### Q8. What problem does the facade solve?
A8. It hides multiple prediction engines behind one simpler API.

### Q9. Where is the Utility pattern used?
A9. In `SmoothScrollUtil`.

### Q10. Why is `SmoothScrollUtil` not placed inside a controller?
A10. Because it is reusable UI behavior, not screen-specific business logic.

### Q11. Is `DataManager` a true Repository?
A11. Not fully. It behaves like a simple persistence layer or repository-like utility, but it is not a full repository abstraction.

### Q12. Why does prediction-core use DTOs?
A12. To keep calculation inputs clean and independent from JavaFX and JSON concerns.

### Q13. Why does prediction-core return result objects?
A13. Result objects make outputs structured, easy to read, and easier to extend with status and explanation fields.

### Q14. Why does prediction-core not depend on JavaFX?
A14. Because it is a business logic layer and should stay reusable and testable.

### Q15. Which design pattern is easiest to see in this project?
A15. MVC is the most obvious, but Service Layer, Mapper, and Facade are also very clear in the prediction integration flow.

## 5. Questions about Architecture

### Q1. Explain the full data flow for target GPA calculation.
A1. The user enters a target GPA in the dashboard. `DashboardController.calculateTargetGrades()` parses the input and calls `PredictionService.calculateRequiredActiveTermGpa(...)`. The service counts active credits, maps the `Student` into `TranscriptInput`, calls `PredictionCoreFacade.calculateRequiredFutureGpa(...)`, receives a `TargetGpaResult`, and the controller formats the UI message.

### Q2. Explain the full data flow for course target grade prediction.
A2. The user enters a target grade in course details. `CourseDetailsController.calculateCourseTarget()` calls `PredictionService.predictCourseTarget(...)`. The service maps the `Course` into `CourseInput`, calls `PredictionCoreFacade.predictCourseTarget(...)`, receives a `CoursePredictionResult`, and the controller updates the UI.

### Q3. How is data saved and loaded?
A3. `DataManager.saveStudent(Student)` writes the object to `student_data.json`, and `DataManager.loadStudent()` reads it back into a `Student` object using Gson.

### Q4. How does JavaFX load screens?
A4. The app uses `FXMLLoader` to load FXML files, create the controller, and build the UI node tree.

### Q5. Why is prediction-core kept in a separate package?
A5. To keep business logic independent from UI, JSON, and controller concerns.

### Q6. Why is the mapper needed?
A6. Because the app model contains UI/persistence-oriented data, while prediction-core expects compact DTOs and a normalized score scale.

### Q7. What happens if an assessment score is `8/10`?
A7. The mapper converts it to `80` before passing it to prediction-core.

### Q8. Why must `8/10` become `80%`?
A8. prediction-core validates and calculates scores on a 0-100 scale.

## 6. Questions about JavaFX

### Q1. What is JavaFX?
A1. JavaFX is a Java UI toolkit for desktop applications.

### Q2. What is FXML?
A2. FXML is an XML-based language for defining JavaFX view layouts.

### Q3. What is a controller?
A3. A controller is a Java class that responds to UI events and updates the view.

### Q4. Why separate FXML and controller?
A4. It keeps layout separate from behavior, which improves maintainability.

### Q5. How do buttons trigger methods?
A5. Through `onAction` bindings in FXML to controller methods annotated with `@FXML`.

### Q6. How does `TableView` connect to a controller?
A6. The controller configures table columns, cell value factories, and sometimes custom cells.

### Q7. How is CSS used?
A7. `style.css` provides shared styling for tables, scrollbars, and UI appearance, while some FXML files still contain inline style strings.

### Q8. How was smooth scrolling improved?
A8. `SmoothScrollUtil` applies animated scrolling behavior to `ScrollPane` nodes.

### Q9. Why are UI utilities separated?
A9. To reuse common JavaFX behavior across screens without duplicating code.

## 7. Questions about Maven and Running the App

### Q1. Why use Maven?
A1. Maven manages dependencies, build steps, test execution, and JavaFX running commands.

### Q2. What does `pom.xml` do?
A2. It defines dependencies, plugin configuration, Java version, and how to launch the app.

### Q3. Why Java 21?
A3. The project is configured for Java 21, and prediction-core uses modern Java features such as `record`.

### Q4. Why use Maven Wrapper?
A4. It lets teammates run the project consistently without installing Maven manually.

### Q5. How do you run the app on macOS/Linux?
A5. `./mvnw clean javafx:run`

### Q6. How do you run the app on Windows?
A6. `.\mvnw.cmd clean javafx:run`

### Q7. Why do prediction-core records require modern Java?
A7. Because `record` is a modern Java feature and the project is explicitly configured for Java 21.

## 8. Questions about Data Persistence

### Q1. Why use JSON?
A1. JSON is simple, readable, easy to debug, and suitable for a course project of this size.

### Q2. What is `student_data.json`?
A2. It is the main data file that stores the serialized `Student` object graph.

### Q3. Why not use a database yet?
A3. JSON is enough for the project scope and easier to demonstrate in class.

### Q4. What are the benefits of JSON here?
A4. It is lightweight, easy to inspect manually, and works well for a single-user desktop app.

### Q5. What are the drawbacks of JSON here?
A5. It is less scalable, less concurrent, and less robust than a database for larger systems.

### Q6. How does `DataManager` support persistence?
A6. It serializes and deserializes the `Student` object using Gson and reads/writes the JSON file.

### Q7. What happens if the JSON file does not exist?
A7. The app opens the welcome/profile-creation flow.

### Q8. What happens if the JSON file is malformed?
A8. This point needs code verification for exact runtime behavior and UI response, but malformed JSON would likely prevent successful loading.

## 9. Questions about prediction-core

### Q1. What is prediction-core?
A1. It is the independent business logic package responsible for course-grade, course-target, GPA projection, and target-GPA calculations.

### Q2. What is `PredictionService`?
A2. It is the app-level service class that exposes prediction use cases to controllers.

### Q3. What is `PredictionMapper`?
A3. It is the adapter that converts app domain models into prediction-core DTOs.

### Q4. What is `PredictionCoreFacade`?
A4. It is the simplified public API over the prediction engines.

### Q5. What is a prediction engine?
A5. It is a class that performs one specific calculation, such as course grade or target GPA.

### Q6. What is a result object?
A6. It is a structured return type such as `GradeResult` or `TargetGpaResult` that contains calculation output and context.

### Q7. How does the app handle an impossible target grade?
A7. The prediction result status becomes `IMPOSSIBLE`, and the controller shows an unreachable-style message.

### Q8. How does validation work?
A8. Input validation exists in both app models and `ValidationPolicy` inside prediction-core.

### Q9. What happens when total weight is over 100%?
A9. prediction-core throws a `PredictionInputException`, and the UI is expected to show a user-friendly message instead of crashing.

### Q10. What happens when target GPA is impossible?
A10. The result status becomes `IMPOSSIBLE`, and the required future GPA may be above 4.0.

## 10. Hard Questions the Instructor May Ask

### Q1. Why did you not use inheritance more?
A1. Because the academic domain is naturally modeled by composition rather than subclass hierarchies.

### Q2. Is this project really object-oriented without many subclasses?
A2. Yes. OOP here is strongest in object modeling, encapsulation, composition, abstraction, and separation of concerns.

### Q3. Does `DataManager` violate SRP?
A3. Not significantly. Its responsibility is persistence. A fuller repository abstraction could improve it later, but its current role is still focused.

### Q4. Are the controllers too large?
A4. Some are, especially `DashboardController` and `CourseDetailsController`. That is one of the main maintainability trade-offs in the current version.

### Q5. Why use a mapper instead of sharing the same `Course` class with prediction-core?
A5. Because the app model is tied to UI and JSON persistence, while prediction-core should remain clean and independent.

### Q6. Why not put prediction-core in the same app package?
A6. A separate package boundary makes the business logic easier to isolate, test, and explain.

### Q7. Why not use a database?
A7. JSON is enough for the current course scope and keeps the project easier to run and demonstrate.

### Q8. Why use JavaFX instead of a console app?
A8. JavaFX allows the project to demonstrate event-driven programming, MVC-style structure, FXML, and UI state management.

### Q9. Why is GPA calculation not entirely inside `Student`?
A9. Some legacy GPA helpers still exist there, but prediction-related GPA logic was moved into prediction-core to keep complex business logic reusable and separate from app-specific models.

### Q10. How would you test this app?
A10. Test prediction-core engines directly, add unit tests for `PredictionMapper` and `PredictionService`, and perform manual UI checks for major workflows.

### Q11. What would you refactor next?
A11. I would reduce controller size, add mapper/service tests, and extract more UI helper code.

### Q12. Where does the app follow OCP?
A12. prediction-core can be extended internally while the UI continues calling the service/facade flow.

### Q13. Where does the app violate SOLID?
A13. The biggest pressure point is controller size and direct instantiation of concrete services.

### Q14. Why is the Facade useful here?
A14. It gives the app one clean entry point to multiple engines.

### Q15. Why is the Adapter/Mapper useful here?
A15. It keeps app data and prediction-core data separate and enforces correct score conversion.

### Q16. What is the difference between official GPA and projected GPA?
A16. Official GPA uses only completed and fully official course evidence, while projected GPA can include active courses that already have graded assessments.

### Q17. How do you avoid fake or invalid predictions?
A17. The mapper passes real app data, and prediction-core validation rejects invalid scores, weights, credits, or targets.

### Q18. How does the app handle invalid input?
A18. Validation errors become exceptions such as `PredictionInputException` or `IllegalArgumentException`, and controllers catch them to show user-friendly feedback.

### Q19. What is the biggest technical limitation?
A19. The UI layer still has some large controllers and some legacy helper logic still coexists with prediction-core.

### Q20. What is the strongest OOP design choice in the project?
A20. The combination of composition in the domain model and separation of prediction logic into service/mapper/facade/core layers.

## 11. Short Presentation Answers

### 30-second app explanation

Academic Tracker is a JavaFX app for managing semesters, courses, assessments, and scores. It also predicts current course grade, target course grade, projected GPA, and target GPA. Data is saved as JSON, and the app runs with Java 21 and Maven.

### 30-second OOP explanation

The app models the academic domain with classes such as `Student`, `Semester`, `Course`, and `Assessment`. It demonstrates encapsulation through private fields and validated setters, and composition through the nested ownership structure of student data.

### 30-second SOLID explanation

The strongest SOLID idea in the app is separation of responsibilities. Controllers handle UI, `DataManager` handles persistence, `PredictionService` handles use cases, `PredictionMapper` handles conversion, and prediction-core handles calculation rules.

### 30-second design pattern explanation

The app uses MVC for screen structure, Service Layer for app-level prediction calls, Mapper/Adapter for data conversion, Facade for hiding prediction engines, and a utility pattern for reusable UI scrolling behavior.

### 30-second prediction-core explanation

prediction-core is an internal business logic package inside the app project. Controllers never call the engines directly. They call `PredictionService`, which maps app models into DTOs, then delegates to `PredictionCoreFacade`, which returns structured result objects.

### 30-second limitation explanation

The architecture is clean overall, but some controllers are still large, and some legacy grade helper methods still coexist with prediction-core. Those are acceptable trade-offs for the current project scope and a good place for future refactoring.

## 12. Final Checklist Before Presentation

- The app opens successfully.
- Dashboard works.
- Course details work.
- Course target prediction works.
- Target GPA prediction works.
- JSON data loads.
- The team can explain OOP concepts clearly.
- The team can explain the service/mapper/facade flow.
- The team can explain why prediction-core is separate from the JavaFX UI layer.
