# Architecture Diagram

```mermaid
flowchart TD
    user["User"]
    fxml["FXML Views<br/>Welcome, Dashboard, Semesters,<br/>CourseRoster, CourseDetails, Settings"]
    controller["UI Controllers<br/>DashboardController, CourseDetailsController, etc."]
    service["PredictionService"]
    mapper["PredictionMapper"]
    facade["PredictionCoreFacade"]
    engines["Prediction Engines<br/>CourseGradeEngine<br/>CoursePredictionEngine<br/>GpaProjectionEngine<br/>TargetGpaEngine"]
    results["Result Objects<br/>GradeResult, CoursePredictionResult,<br/>GpaProjectionResult, TargetGpaResult"]
    models["Domain Model<br/>Student, Semester, Course, Assessment"]
    persistence["DataManager"]
    json["student_data.json"]
    util["SmoothScrollUtil"]

    user --> fxml
    fxml --> controller
    controller --> models
    controller --> service
    service --> mapper
    mapper --> models
    mapper --> facade
    service --> facade
    facade --> engines
    engines --> results
    results --> controller
    controller --> fxml

    controller --> persistence
    persistence --> json
    persistence --> models

    controller --> util
```

Key rule:

- UI/controllers call prediction through `PredictionService`, `PredictionMapper`, and `PredictionCoreFacade`.
- `com.academictracker.prediction2` stays independent from JavaFX, FXML, Gson, and controllers.

