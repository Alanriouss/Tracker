# Comprehensive Architectural Report: Academic Tracker

**Team Members:**
* Huỳnh Bá Dũng - ITDSIU25012
* Vũ Tiến Dũng - ITDSIU25011
* Mai Hoàng Gia Bảo - ITDSIU25005
* Nguyễn Phạm Nhật Minh - ITDSIU25022

**GitHub Repository:** [Alanriouss/Tracker](https://github.com/Alanriouss/Tracker)

---
## 1. Introduction & High-Level System Architecture
The **Academic Tracker** application is a Java 21 and JavaFX project structured around an academic domain: students, semesters, courses, assessments, GPA calculation, and grade prediction. Its object-oriented structure is shaped by two critical architectural decisions:

1. **UI & Persistence Layer:** The JavaFX application UI and persistence logic live under `com.tracker.academictracker`.
2. **Business Logic Layer:** The mathematical prediction engine lives completely separately under `com.academictracker.prediction2`.
This separation is vital because the user interface layer should not directly contain complex grade, GPA, or prediction algorithms. Instead, the JavaFX controllers communicate through application-level integration classes (Services and Mappers), which then interact with a central Facade. 
This report analyzes the project exclusively through three lenses: Object-Oriented Programming (OOP) concepts, SOLID design principles, and Design Patterns.
### 1.1 Architectural Component Diagram
This diagram illustrates the macro-level dependencies of the entire project, showcasing the strict isolation between the UI layer and the prediction core.

![[Overview.png]]
---
![[stystem architecture.png]]

---

## 2. Object-Oriented Programming (OOP) Concepts

### 2.1 Classes and Objects
The core domain model translates the real-world academic environment into code through classes like `Student`, `Semester`, `Course`, and `Assessment`. 
These classes model the academic environment in a direct, object-oriented way. Rather than passing disconnected primitive values (like raw arrays of grades) throughout the application, the code works with domain objects that bundle state and behavior. 
This is an appropriate model because the real-world academic structure is naturally hierarchical:
* `Student` contains `Semesters`
* `Semester` contains `Courses`
* `Course` contains `Assessments`
The domain objects also contain meaningful behavior related to their data. For example:
* `Student` has methods such as `addSemester()`, `removeSemester()`, and `getCumulativeGPA()`.
* `Course` groups together the course code, name, credit value, status, and assessment list, providing methods like `getTotalWeight()` and `getCurrentGrade()`.
### 2.2 Advanced Encapsulation
Encapsulation in this project goes beyond standard getters and setters; it acts as a strict gatekeeper for data integrity using private fields, public accessors, and validation logic.
For instance, the `Assessment` class protects its internal state (`score`, `weight`, `maxScore`). The fields are not directly modified from the outside. Instead, it enforces rules directly within its mutators:
* `weight` cannot be negative or exceed 100.
* `maxScore` must be greater than 0.
* `score` cannot logically exceed its max score.
By encapsulating this validation logic, the UI layer does not need to repeatedly check these rules—the object protects itself. Similarly, `Course` encapsulates its assessment collection, exposing methods like `addAssessment()` so outside code does not need to manually calculate the course weight.
### 2.3 Composition Over Inheritance
The domain model relies heavily on **Composition** (a "has-a" relationship) rather than deep, brittle inheritance trees (an "is-a" relationship). This is one of the strongest object-oriented decisions in the project.
A `Semester` is not a type of `Student`; it is *owned* by a `Student`. Using inheritance here (e.g., `Course extends Semester`) would create an unnatural model. Composition provides several benefits:
* It matches the real academic structure.
* It keeps each class focused on its own responsibility.
* It is highly effective for JSON serialization, allowing nested data to be saved easily.
![[model.png]]

### 2.4 Abstraction and Polymorphism
Abstraction hides complex details behind a simpler public interface. The UI controllers do not instantiate `TargetGpaEngine` or `CourseGradeEngine` directly. Instead, they call `PredictionService`, hiding the complex mathematical DTOs and validation policies from the front-end. 

While custom polymorphism is minimal due to the preference for composition, framework-level polymorphism is heavily used in JavaFX. For example, `Main` extends the JavaFX `Application` class, and utilities like `SmoothScrollUtil.applyTo(Node root)` accept a general `Node` to recursively handle specific children dynamically.

---
## 3. Application of SOLID Principles
### 3.1 Single Responsibility Principle (SRP)
SRP dictates that a class should have only one reason to change. The project excels here by strictly separating concerns:
* **`DataManager`**: Its sole responsibility is translating Java objects to and from the `student_data.json` file.
* **`PredictionMapper`**: Its sole responsibility is adapting the application `Course` model into the core `CourseInput` DTO.
* **UI Controllers**: Handle purely view updates and event listeners (e.g., `DashboardController`).
### 3.2 Open/Closed Principle (OCP)
The `prediction-core` is a textbook example of OCP. Calculation logic is separated into specific engine classes (`CourseGradeEngine`, `TargetGpaEngine`, etc.). If new calculation methods are needed (e.g., Weighted GPA for honors classes), developers do not need to modify the existing engines or the `Student` model. They simply create a new engine class and expose it through the `PredictionCoreFacade`. The existing, tested code remains closed to modification but open to extension.
### 3.3 Liskov Substitution Principle (LSP) & Interface Segregation Principle (ISP)
Because the project wisely avoids deep custom inheritance hierarchies in favor of composition, LSP is not heavily tested here. Similarly, the project avoids bloated "god interfaces." While the lack of custom interfaces limits Dependency Injection, it ensures ISP is not violated by forcing classes to implement unused methods.
### 3.4 Dependency Inversion Principle (DIP)
The UI components (`DashboardController`) do not depend on low-level calculation modules. Instead, both depend on an abstraction layer (`PredictionService`). While strict Java interfaces are not used, this layered architecture achieves the primary goal of DIP: decoupling the high-level UI from the low-level math. 

---
## 4. Design Patterns in Practice
### 4.1 Model-View-Controller (MVC) Pattern
The project heavily utilizes MVC through JavaFX:
* **View:** FXML files define the layout (e.g., `Dashboard.fxml`, `CourseDetails.fxml`).
* **Controller:** Java classes handle UI events and update fields (e.g., `DashboardController`).
* **Model:** Domain classes store the data state (`Semester`, `Course`).
### 4.2 The Facade Pattern
The `PredictionCoreFacade` provides a unified, clean interface to a highly complex subsystem. The application layer doesn't need to know how grade scales are applied, how inputs are validated, or which specific engine handles the math. It simply calls high-level methods seamlessly.
### 4.3 The Mapper / Adapter Pattern
`PredictionMapper` acts as a Mapper and Adapter between the JavaFX application's domain model and the prediction-core DTO model. Crucially, it normalizes assessment scores (converting a raw score of 8/10 into a standardized 80.0%) so the engine can process the math correctly without knowing about app-level data structures.
### 4.4 The Service Layer Pattern & Sequence Execution
`PredictionService` coordinates the Mapper and the Facade, matching actual application use cases. This means the UI controller does not need to perform DTO mapping manually, keeping the controller strictly focused on UI behavior.
![[4.4.png]]

### 4.5 Persistence Pattern
`DataManager` acts as the persistence boundary. Because the Domain Model relies on pure composition, serializing the single root `Student` object automatically cascades and saves all nested `Semesters`, `Courses`, and `Assessments` to JSON.

![[4.5.png]]

### 4.6 Utility Pattern
`SmoothScrollUtil` provides reusable scroll behavior for JavaFX `ScrollPane` nodes. Extracting this into a utility class prevents the project from duplicating animation setup code across multiple controllers.

---

## 5. Comprehensive UML & Mind Map

The following diagrams provide a complete structural overview of the project's classes and architecture. 
![[mindmap.png]]

