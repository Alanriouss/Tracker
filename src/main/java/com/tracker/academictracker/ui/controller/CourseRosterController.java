package com.tracker.academictracker.ui.controller;

import com.tracker.academictracker.domain.model.Course;
import com.tracker.academictracker.domain.model.CourseStatus;
import com.tracker.academictracker.domain.model.Semester;
import com.tracker.academictracker.domain.model.Student;
import com.tracker.academictracker.persistence.DataManager;
import com.tracker.academictracker.ui.util.SmoothScrollUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class CourseRosterController {

    @FXML private VBox addCourseForm;
    @FXML private TextField courseCodeInput;
    @FXML private TextField courseNameInput;
    @FXML private TextField creditsInput;
    @FXML private ComboBox<CourseStatus> statusInput; // Thay đổi thành CourseStatus

    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> colCode;
    @FXML private TableColumn<Course, String> colName;
    @FXML private TableColumn<Course, Integer> colCredits;
    @FXML private TableColumn<Course, CourseStatus> colStatus;
    @FXML private TableColumn<Course, String> colGrade; // Cột mới
    @FXML private TableColumn<Course, Void> colAction;

    private Student student;
    private Semester activeSemester; // Học kỳ đang được chọn từ Dashboard truyền sang
    private BorderPane mainContentPane;
    /**
     * Nhận dữ liệu Student và Học kỳ đang Active
     */
    public void initData(Student student, Semester activeSemester, BorderPane mainContentPane) {
        this.student = student;
        this.activeSemester = activeSemester;
        this.mainContentPane = mainContentPane;

        // Khởi tạo ComboBox Status
        if (statusInput != null) {
            statusInput.setItems(FXCollections.observableArrayList(CourseStatus.values()));
            statusInput.getSelectionModel().selectFirst();
        }

        setupTable();
        courseTable.setRowFactory(tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // Nhấn đúp chuột (Click Count == 2) vào hàng không rỗng
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Course clickedCourse = row.getItem();
                    openCourseDetails(clickedCourse);
                }
            });
            return row;
        });
        updateTable();
    }
    private void openCourseDetails(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tracker/academictracker/CourseDetails.fxml"));
            javafx.scene.Node view = loader.load();
            SmoothScrollUtil.applyTo(view);

            CourseDetailsController controller = loader.getController();
            controller.initData(student, activeSemester, course, mainContentPane);

            mainContentPane.setCenter(view);
        } catch (IOException e) {
            System.err.println("❌ Lỗi không thể mở trang Course Details!");
            e.printStackTrace();
        }
    }

    private void setupTable() {
        // Ánh xạ dữ liệu cơ bản
        colCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));

        // 1. Tùy chỉnh cột STATUS (Tạo Badge màu xanh/vàng/đỏ)
        // Trong setupTable() của CourseRosterController.java[cite: 9]
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(CourseStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    // Biến enum thành chữ viết hoa để so sánh cho chính xác
                    String statusStr = status.toString().toUpperCase();
                    Label badge = new Label(statusStr.replace("_", " ")); // "IN_PROGRESS" -> "IN PROGRESS"
                    badge.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 5;");

                    if (statusStr.contains("COMPLETED")) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #d1fae5; -fx-text-fill: #059669;");
                    } else if (statusStr.contains("PROGRESS") || statusStr.contains("ACTIVE")) {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #e0f2fe; -fx-text-fill: #0284c7;");
                    } else {
                        badge.setStyle(badge.getStyle() + "-fx-background-color: #f3f4f6; -fx-text-fill: #4b5563;");
                    }
                    setGraphic(badge);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // 2. Tùy chỉnh cột CURRENT GRADE 

        // 2. Tùy chỉnh cột CURRENT GRADE
        colGrade.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(""));

        colGrade.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String grade, boolean empty) {
                super.updateItem(grade, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Course course = (Course) getTableRow().getItem();
                    double currentGrade = course.getCurrentGrade();

                    boolean hasGraded = course.getAssessments().stream().anyMatch(com.tracker.academictracker.domain.model.Assessment::isGraded);

                    if (currentGrade == 0.0 && !hasGraded) {
                        setText("N/A");
                    } else {
                        // ✅ FIXED: Round the grade to the nearest whole integer
                        long roundedGrade = Math.round(currentGrade);
                        setText(roundedGrade + "%");
                    }
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // 3. Tùy chỉnh cột ACTION (Nút Xóa hình thùng rác)
        colAction.setCellFactory(column -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑");

            {
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-size: 16px; -fx-cursor: hand;");
                deleteBtn.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    handleDeleteCourse(course);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    private void updateTable() {
        if (activeSemester != null && activeSemester.getCourses() != null) {
            courseTable.setItems(FXCollections.observableArrayList(activeSemester.getCourses()));
        } else {
            courseTable.getItems().clear();
        }
    }

    // --- FORM ACTIONS ---

    @FXML
    private void handleAddCourseClick() {
        addCourseForm.setVisible(true);
        addCourseForm.setManaged(true);
    }

    @FXML
    private void hideAddForm() {
        addCourseForm.setVisible(false);
        addCourseForm.setManaged(false);
        courseCodeInput.clear();
        courseNameInput.clear();
        creditsInput.clear();
    }

    @FXML
    private void saveNewCourse() {
        if (activeSemester == null) return;

        try {
            String code = courseCodeInput.getText().trim();
            String name = courseNameInput.getText().trim();
            int credits = Integer.parseInt(creditsInput.getText().trim());
            CourseStatus status = statusInput.getValue(); // Lấy enum status[cite: 3]

            if (code.isEmpty() || name.isEmpty()) return;

            // Điều chỉnh Constructor theo thiết kế Course.java của bạn
            Course newCourse = new Course();
            newCourse.setCourseCode(code);
            newCourse.setCourseName(name);
            newCourse.setCredits(credits);
            newCourse.setStatus(status);

            activeSemester.addCourse(newCourse);
            DataManager.saveStudent(student); // Lưu vào JSON

            updateTable();
            hideAddForm();
            System.out.println("✅ Course Added!");

        } catch (NumberFormatException | IOException e) {
            System.err.println("❌ Lỗi dữ liệu hoặc lưu file!");
        }
    }

    private void handleDeleteCourse(Course course) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Course");
        alert.setHeaderText("Are you sure you want to delete " + course.getCourseCode() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            activeSemester.getCourses().remove(course);
            try {
                DataManager.saveStudent(student);
                updateTable();
            } catch (IOException e) {
                System.err.println("❌ Lỗi khi lưu sau khi xóa!");
            }
        }
    }
}
