package com.tracker.academictracker.ui.controller;
import com.academictracker.prediction2.exception.PredictionInputException;
import com.academictracker.prediction2.result.GpaProjectionResult;
import com.academictracker.prediction2.result.TargetGpaResult;
import com.tracker.academictracker.domain.model.Course;
import com.tracker.academictracker.domain.model.Semester;
import com.tracker.academictracker.domain.model.Student;
import com.tracker.academictracker.persistence.DataManager;
import com.tracker.academictracker.service.PredictionService;
import com.tracker.academictracker.ui.util.SmoothScrollUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public class DashboardController {

    // --- FXML Elements (Header) ---
    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label academicStandingLabel;
    @FXML private ComboBox<Semester> activeSemesterComboBox; // Updated ID

    // --- FXML Elements (Widgets/Stats) ---
    @FXML private Label termGpaLabel;
    @FXML private Label overallGpaLabel; // Updated ID

    // --- FXML Elements (Predictive Analysis) ---
    @FXML private TextField targetGpaInput;
    @FXML private Label predictionResultLabel;

    // --- FXML Elements (Lists) ---
    // Note: We need to assign this ID to the VBox inside the "ACTIVE COURSES SECTION" in FXML
    @FXML private VBox activeCoursesContainer;
    @FXML private BorderPane mainContentPane; // Khung chứa chính để đổi trang
    @FXML private VBox dashboardContent;
    @FXML private ScrollPane dashboardScrollPane;
    @FXML private Button btnDashboard;
    @FXML private Button btnSemesters;
    @FXML private Button btnCourseRoster;
    @FXML private Button btnSettings;
    private Student student;
    private final PredictionService predictionService = new PredictionService();

    public void initData(Student student) {
        this.student = student;

        if (student != null) {
            // Nạp thông tin cơ bản
            studentNameLabel.setText(student.getFullName());
            studentIdLabel.setText("ID: " + student.getStudentId());
            updateAcademicStanding();

            // Cập nhật Overall GPA
            updateOverallGpaLabel();

            // Cấu hình Menu chọn Học kỳ
            setupActiveSemesterCombo();
            SmoothScrollUtil.applyTo(dashboardScrollPane);
        }
    }

    private void updateOverallGpaLabel() {
        if (student == null) {
            overallGpaLabel.setText("N/A");
            return;
        }

        try {
            GpaProjectionResult result = predictionService.projectStudentGpa(student);
            if (result.projectedCredits() == 0) {
                overallGpaLabel.setText("N/A");
            } else {
                overallGpaLabel.setText(String.format("%.2f", result.projectedGpa()));
            }
        } catch (PredictionInputException e) {
            overallGpaLabel.setText("Invalid data");
        } catch (IllegalArgumentException e) {
            overallGpaLabel.setText("Invalid data");
        }
    }

    private void updateAcademicStanding() {
        String standing = student.getAcademicStanding();
        academicStandingLabel.setText(standing);

        // Đổi màu tùy theo trạng thái
        if (standing.equals("Good Standing")) {
            academicStandingLabel.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-padding: 5 15 5 15; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 13px;");
        } else if (standing.equals("Satisfactory")) {
            academicStandingLabel.setStyle("-fx-background-color: #fef08a; -fx-text-fill: #ca8a04; -fx-padding: 5 15 5 15; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 13px;");
        } else {
            academicStandingLabel.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #dc2626; -fx-padding: 5 15 5 15; -fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 13px;");
        }
    }

    private void setupActiveSemesterCombo() {
        if (student.getSemesters() == null || student.getSemesters().isEmpty()) {
            return;
        }

        ObservableList<Semester> semesters = FXCollections.observableArrayList(student.getSemesters());
        activeSemesterComboBox.setItems(semesters);

        activeSemesterComboBox.setConverter(new StringConverter<Semester>() {
            @Override
            public String toString(Semester semester) {
                return semester != null ? semester.getDisplayName() : "";
            }
            @Override
            public Semester fromString(String string) { return null; }
        });

        String savedActiveName = student.getActiveSemesterName();
        Semester semesterToSelect = null;

        for (Semester s : semesters) {
            if (s.getDisplayName().equals(savedActiveName)) {
                semesterToSelect = s;
                break;
            }
        }

        if (semesterToSelect != null) {
            activeSemesterComboBox.getSelectionModel().select(semesterToSelect);
        } else {
            activeSemesterComboBox.getSelectionModel().selectFirst();
            student.setActiveSemesterName(activeSemesterComboBox.getValue().getDisplayName());
        }

        activeSemesterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                student.setActiveSemesterName(newVal.getDisplayName());

                try {
                    DataManager.saveStudent(student);
                } catch (IOException e) {
                    System.err.println("❌ Lỗi khi lưu Active Semester từ Dashboard!");
                }

                updateTermWidgets(newVal);
            }
        });

        updateTermWidgets(activeSemesterComboBox.getValue());
    }

    private void updateTermWidgets(Semester activeSemester) {
        if (activeSemester == null) return;

        // 1. Cập nhật Term GPA
        try {
            GpaProjectionResult result = predictionService.projectSemesterGpa(activeSemester);
            if (result.projectedCredits() == 0) {
                termGpaLabel.setText("N/A");
            } else {
                termGpaLabel.setText(String.format("%.2f", result.projectedGpa()));
            }
        } catch (PredictionInputException e) {
            termGpaLabel.setText("Check course weights");
        } catch (IllegalArgumentException e) {
            termGpaLabel.setText("Invalid data");
        }

        // 2. Vẽ danh sách môn học của học kỳ này
        if (activeCoursesContainer != null) {
            activeCoursesContainer.getChildren().clear();

            if (activeSemester.getCourses() == null || activeSemester.getCourses().isEmpty()) {
                Label emptyLabel = new Label("No courses for this semester. Add courses from the Course Roster.");
                emptyLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 15px;");
                VBox centerWrapper = new VBox(emptyLabel);
                centerWrapper.setAlignment(javafx.geometry.Pos.CENTER);
                centerWrapper.setPrefHeight(120.0);
                activeCoursesContainer.getChildren().add(centerWrapper);
                return;
            }

            for (Course course : activeSemester.getCourses()) {
                activeCoursesContainer.getChildren().add(createCourseMiniCard(course));
            }
        }
    }

    private HBox createCourseMiniCard(Course course) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
        card.setPadding(new Insets(12, 15, 12, 15));

        VBox textInfo = new VBox(2);
        Label codeLabel = new Label(course.getCourseCode());
        codeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-font-size: 14px;");

        Label nameLabel = new Label(course.getCourseName());
        nameLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        textInfo.getChildren().addAll(codeLabel, nameLabel);
        card.getChildren().addAll(textInfo);

        return card;
    }

    // Placeholder cho hàm tính toán Predictive Analysis
    @FXML
    private void calculateTargetGrades() {
        try {
            String input = targetGpaInput.getText().trim();
            if (input.isEmpty()) return;

            double targetGpa = Double.parseDouble(input);

            Semester activeSem = findActiveSemester();
            if (activeSem == null || activeSem.getCourses() == null || activeSem.getCourses().isEmpty()) {
                predictionResultLabel.setText("Please add courses to your active semester first.");
                predictionResultLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            TargetGpaResult result = predictionService.calculateRequiredActiveTermGpa(student, activeSem, targetGpa);
            switch (result.status()) {
                case IMPOSSIBLE -> {
                    predictionResultLabel.setText(String.format(
                            "Unreachable. You need a term GPA of %.2f.",
                            result.requiredFutureGpa()
                    ));
                    predictionResultLabel.setStyle("-fx-text-fill: #ef4444;");
                }
                case ALREADY_ACHIEVED -> {
                    predictionResultLabel.setText("Target secured! Your official GPA already meets this target.");
                    predictionResultLabel.setStyle("-fx-text-fill: #10b981;");
                }
                case ACHIEVABLE -> {
                    predictionResultLabel.setText(String.format(
                            "You need a term GPA of %.2f this semester.",
                            result.requiredFutureGpa()
                    ));
                    predictionResultLabel.setStyle("-fx-text-fill: #0284c7;");
                }
            }

        } catch (NumberFormatException e) {
            predictionResultLabel.setText("Please enter a valid number (e.g., 3.5).");
            predictionResultLabel.setStyle("-fx-text-fill: #ef4444;");
        } catch (PredictionInputException e) {
            predictionResultLabel.setText(userFriendlyPredictionError(e));
            predictionResultLabel.setStyle("-fx-text-fill: #ef4444;");
        } catch (IllegalArgumentException e) {
            predictionResultLabel.setText(userFriendlyPredictionError(e));
            predictionResultLabel.setStyle("-fx-text-fill: #ef4444;");
        }
    }

    private String userFriendlyPredictionError(IllegalArgumentException e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            return "Please check your course data and try again.";
        }
        if (message.contains("No active credits")) {
            return "No active courses available to calculate against.";
        }
        if (message.contains("weight")) {
            return "Check course weights before calculating a target GPA.";
        }
        return "Please check your course data and try again.";
    }
    // --- XỬ LÝ CHUYỂN TRANG (NAVIGATION) ---
    @FXML
    private void handleNavigation(ActionEvent event) {
        javafx.scene.control.Button clickedButton = (javafx.scene.control.Button) event.getSource();
        String buttonId = clickedButton.getId();

        // --- BƯỚC 1: CẬP NHẬT GIAO DIỆN MENU (AESTHETIC) ---
        if (btnDashboard != null) btnDashboard.getStyleClass().remove("sidebar-button-active");
        if (btnSemesters != null) btnSemesters.getStyleClass().remove("sidebar-button-active");
        if (btnCourseRoster != null) btnCourseRoster.getStyleClass().remove("sidebar-button-active");
        if (btnSettings != null) btnSettings.getStyleClass().remove("sidebar-button-active");

        clickedButton.getStyleClass().add("sidebar-button-active");

        // --- BƯỚC 2: LOGIC CHUYỂN TRANG ---
        try {
            if ("btnDashboard".equals(buttonId)) {
                SmoothScrollUtil.applyTo(dashboardScrollPane);
                mainContentPane.setCenter(dashboardScrollPane);

                // Làm mới dữ liệu Dashboard
                setupActiveSemesterCombo();
                updateOverallGpaLabel();
                updateAcademicStanding();

            } else if ("btnSemesters".equals(buttonId)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tracker/academictracker/Semesters.fxml"));
                Node view = loader.load();
                SmoothScrollUtil.applyTo(view);

                SemestersController controller = loader.getController();
                
                
                controller.initData(student, this::setupActiveSemesterCombo);

                mainContentPane.setCenter(view);

            } else if ("btnCourseRoster".equals(buttonId)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tracker/academictracker/CourseRoster.fxml"));
                Node view = loader.load();
                SmoothScrollUtil.applyTo(view);

                CourseRosterController controller = loader.getController();

                // Tìm Semester đang hoạt động
                Semester activeSem = findActiveSemester();

                controller.initData(student, activeSem, mainContentPane);

                mainContentPane.setCenter(view);
            } else if ("btnSettings".equals(buttonId)) { // ✅ Add this new block!
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tracker/academictracker/Settings.fxml"));
                Node view = loader.load();
                SmoothScrollUtil.applyTo(view);

                SettingsController controller = loader.getController();
                controller.initData(student);

                mainContentPane.setCenter(view);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tải giao diện: " + buttonId);
            e.printStackTrace();
        }
    }
    
    /**
     * Hàm hỗ trợ tìm nhanh Semester đang Active (Phòng trường hợp bạn chưa thêm hàm này)
     */
    private Semester findActiveSemester() {
        if (student.getSemesters() == null) return null;
        return student.getSemesters().stream()
                .filter(s -> s.getDisplayName().equals(student.getActiveSemesterName()))
                .findFirst()
                .orElse(null);
    }
}
