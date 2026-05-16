package com.tracker.academictracker.ui.controller;

import com.academictracker.prediction2.exception.PredictionInputException;
import com.academictracker.prediction2.result.CoursePredictionResult;
import com.academictracker.prediction2.result.GradeResult;
import com.tracker.academictracker.domain.model.Assessment;
import com.tracker.academictracker.domain.model.Course;
import com.tracker.academictracker.domain.model.Semester;
import com.tracker.academictracker.domain.model.Student;
import com.tracker.academictracker.persistence.DataManager;
import com.tracker.academictracker.service.PredictionService;
import com.tracker.academictracker.ui.util.SmoothScrollUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class CourseDetailsController {

    // Labels
    @FXML private Label courseCodeLabel;
    @FXML private Label courseNameLabel;
    @FXML private Label creditsLabel;
    @FXML private Label statusLabel;
    @FXML private Label currentGradeLabel;
    @FXML private Label totalWeightLabel;
    @FXML private TextField targetCourseGradeInput;
    @FXML private Label coursePredictionResultLabel;

    // Inputs
    @FXML private TextField assessNameInput;
    @FXML private ComboBox<String> categoryInput;
    @FXML private TextField weightInput;
    @FXML private TextField maxScoreInput;
    @FXML private TextField scoreInput;

    // Table
    @FXML private TableView<Assessment> assessmentTable;
    @FXML private TableColumn<Assessment, String> colAssessName;
    @FXML private TableColumn<Assessment, String> colCategory;
    @FXML private TableColumn<Assessment, Double> colWeight;
    @FXML private TableColumn<Assessment, Assessment> colScore;
    @FXML private TableColumn<Assessment, Assessment> colAction;

    // Data references
    private Student student;
    private Semester activeSemester;
    private Course currentCourse;
    private BorderPane mainContentPane; // Dùng để đổi trang ngược lại
    private final PredictionService predictionService = new PredictionService();

    public void initData(Student student, Semester activeSemester, Course course, BorderPane mainContentPane) {
        this.student = student;
        this.activeSemester = activeSemester;
        this.currentCourse = course;
        this.mainContentPane = mainContentPane;

        categoryInput.setItems(FXCollections.observableArrayList("Exam", "Quiz", "Assignment", "Project", "Other"));
        categoryInput.getSelectionModel().selectFirst();

        updateCourseInfo();
        setupTable();
        refreshData();
    }

    private void updateCourseInfo() {
        courseCodeLabel.setText(currentCourse.getCourseCode());
        courseNameLabel.setText(currentCourse.getCourseName());
        creditsLabel.setText(String.valueOf(currentCourse.getCredits()));
        statusLabel.setText(currentCourse.getStatus().toString());
    }

    private void refreshData() {
        // Cập nhật Table
        if (currentCourse.getAssessments() != null) {
            assessmentTable.setItems(FXCollections.observableArrayList(currentCourse.getAssessments()));
        }

        // Cập nhật Total Weight
        double totalWeight = currentCourse.getTotalWeight();
        totalWeightLabel.setText(totalWeight + "%");
        if (totalWeight > 100.0) {
            totalWeightLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 18px;");
        } else {
            totalWeightLabel.setStyle("-fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-font-size: 18px;");
        }

        // Cập nhật Current Grade
        if (!hasGradedAssessments()) {
            currentGradeLabel.setText("N/A");
            currentGradeLabel.setStyle("-fx-text-fill: #94a3b8;");
        } else {
            try {
                GradeResult gradeResult = predictionService.calculateCourseGrade(currentCourse);
                currentGradeLabel.setText(Math.round(gradeResult.score100()) + "%");
                currentGradeLabel.setStyle("-fx-text-fill: #2563eb;");
            } catch (PredictionInputException e) {
                currentGradeLabel.setText("Check weights");
                currentGradeLabel.setStyle("-fx-text-fill: #ef4444;");
            } catch (IllegalArgumentException e) {
                currentGradeLabel.setText("Invalid");
                currentGradeLabel.setStyle("-fx-text-fill: #ef4444;");
            }
        }
    }

    private boolean hasGradedAssessments() {
        return currentCourse.getAssessments() != null
                && currentCourse.getAssessments().stream().anyMatch(Assessment::isGraded);
    }

    private void setupTable() {
        colAssessName.setCellValueFactory(new PropertyValueFactory<>("assessmentName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));

        // Custom cột Score: Hiển thị dạng "85.0 / 100.0"
        colScore.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue()));
        colScore.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Assessment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item.isGraded()) {
                        setText(item.getScore() + " / " + item.getMaxScore());
                        setStyle("-fx-text-fill: #0f172a;");
                    } else {
                        setText("N/A / " + item.getMaxScore());
                        setStyle("-fx-text-fill: #94a3b8;");
                    }
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Custom cột Action: Nút Xóa
        colAction.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue()));
        colAction.setCellFactory(column -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(10, editBtn, deleteBtn);

            {
                pane.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563eb; -fx-font-size: 16px; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 16px; -fx-cursor: hand;");

                // --- NEW: EDIT BUTTON LOGIC ---
                editBtn.setOnAction(e -> {
                    Assessment a = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(a.isGraded() ? String.valueOf(a.getScore()) : "");
                    dialog.setTitle("Edit Score");
                    dialog.setHeaderText("Update score for: " + a.getAssessmentName());
                    dialog.setContentText("Enter new score (Max: " + a.getMaxScore() + ")\nLeave blank to mark as ungraded:");

                    dialog.showAndWait().ifPresent(newScore -> {
                        try {
                            if (newScore.trim().isEmpty()) {
                                a.setScore(null); // Removes the score so it goes back to N/A
                            } else {
                                double score = Double.parseDouble(newScore.trim());
                                if (score < 0 || score > a.getMaxScore()) {
                                    throw new IllegalArgumentException("Score out of bounds.");
                                }
                                a.setScore(score);
                            }
                            // Save to hard drive and refresh UI
                            saveData();
                            refreshData();
                            getTableView().refresh();
                        } catch (Exception ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid score. It must be a number between 0 and " + a.getMaxScore() + ".");
                            alert.show();
                        }
                    });
                });

                // --- EXISTING: DELETE BUTTON LOGIC ---
                deleteBtn.setOnAction(e -> {
                    Assessment a = getTableView().getItems().get(getIndex());
                    currentCourse.removeAssessment(a);
                    saveData();
                    refreshData();
                });
            }

            @Override
            protected void updateItem(Assessment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    @FXML
    private void saveAssessment() {
        try {
            String name = assessNameInput.getText().trim();
            String category = categoryInput.getValue();
            double weight = Double.parseDouble(weightInput.getText().trim());
            double maxScore = Double.parseDouble(maxScoreInput.getText().trim());

            if (name.isEmpty()) return;

            Assessment newAss = new Assessment(name, category, weight, maxScore);

            // Xử lý điểm (Có thể để trống nếu chưa có điểm)
            String scoreTxt = scoreInput.getText().trim();
            if (!scoreTxt.isEmpty()) {
                newAss.setScore(Double.parseDouble(scoreTxt));
            }

            currentCourse.addAssessment(newAss);
            saveData();
            refreshData();

            // Clear input
            assessNameInput.clear(); weightInput.clear(); maxScoreInput.clear(); scoreInput.clear();
        } catch (Exception e) {
            System.err.println("❌ Lỗi nhập liệu Assessment!");
        }
    }

    private void saveData() {
        try {
            DataManager.saveStudent(student);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Nút QUAY LẠI TRANG COURSE ROSTER
    @FXML
    private void handleBackToRoster() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tracker/academictracker/CourseRoster.fxml"));
            Node view = loader.load();
            SmoothScrollUtil.applyTo(view);
            CourseRosterController controller = loader.getController();

            // Trả lại BorderPane cho Roster
            controller.initData(student, activeSemester, mainContentPane);
            mainContentPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void calculateCourseTarget() {
        try {
            String input = targetCourseGradeInput.getText().trim();
            if (input.isEmpty()) return;

            double targetGrade = Double.parseDouble(input);
            CoursePredictionResult result = predictionService.predictCourseTarget(currentCourse, targetGrade);

            switch (result.status()) {
                case IMPOSSIBLE -> {
                    coursePredictionResultLabel.setText(String.format(
                            "Unreachable. You would need %.1f%% on the remaining assignments.",
                            result.requiredRemainingAverage()
                    ));
                    coursePredictionResultLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                }
                case ALREADY_ACHIEVED -> {
                    coursePredictionResultLabel.setText(
                            "Target secured! You have already earned enough points for this grade."
                    );
                    coursePredictionResultLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                }
                case ACHIEVABLE -> {
                    coursePredictionResultLabel.setText(String.format(
                            "You need an average of %.1f%% on the remaining %.0f%% of the course.",
                            result.requiredRemainingAverage(),
                            result.remainingWeightPercent()
                    ));
                    coursePredictionResultLabel.setStyle("-fx-text-fill: #0284c7; -fx-font-weight: bold;");
                }
            }

        } catch (NumberFormatException e) {
            coursePredictionResultLabel.setText("Please enter a valid number (e.g., 85).");
            coursePredictionResultLabel.setStyle("-fx-text-fill: #ef4444;");
        } catch (PredictionInputException e) {
            coursePredictionResultLabel.setText(userFriendlyPredictionError(e));
            coursePredictionResultLabel.setStyle("-fx-text-fill: #ef4444;");
        } catch (IllegalArgumentException e) {
            coursePredictionResultLabel.setText(userFriendlyPredictionError(e));
            coursePredictionResultLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    private String userFriendlyPredictionError(IllegalArgumentException e) {
        String message = e.getMessage();
        if (message != null && message.contains("weight")) {
            return "Check weights before calculating a target grade.";
        }
        return "Please check this course's assessment data and try again.";
    }
}
