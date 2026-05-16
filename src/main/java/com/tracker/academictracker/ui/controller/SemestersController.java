package com.tracker.academictracker.ui.controller;

import com.tracker.academictracker.domain.model.Semester;
import com.tracker.academictracker.domain.model.Student;
import com.tracker.academictracker.persistence.DataManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SemestersController {

    // --- FXML Elements ---
    @FXML private VBox semesterListContainer; // Container chứa danh sách thẻ học kỳ
    @FXML private VBox addSemesterForm;      // VBox chứa form nhập liệu (ẩn/hiện)
    @FXML private TextField yearInput;        // Ô nhập năm
    @FXML private ComboBox<String> termInput; // Menu chọn mùa (Season)
    private Runnable onActiveSemesterChanged;
    private Student student;

    public void initData(Student student, Runnable onActiveSemesterChanged) {
        this.student = student;
        this.onActiveSemesterChanged = onActiveSemesterChanged; // Store the callback

        if (termInput != null) {
            termInput.setItems(FXCollections.observableArrayList("Sem 1", "Sem 2", "Summer"));
            termInput.getSelectionModel().selectFirst();
        }

        loadCards();
    }

    @FXML
    private void handleAddSemester() {
        addSemesterForm.setVisible(true);
        addSemesterForm.setManaged(true);
    }

    @FXML
    private void hideAddForm() {
        addSemesterForm.setVisible(false);
        addSemesterForm.setManaged(false);
        yearInput.clear();
    }

    /**
     * Lưu học kỳ mới và cập nhật UI/JSON[cite: 6, 7]
     */
    @FXML
    private void saveNewSemester() {
        try {
            String yearText = yearInput.getText().trim();
            if (yearText.isEmpty()) return;

            int year = Integer.parseInt(yearText);
            String term = termInput.getValue();

            // 1. Tạo và thêm vào bộ nhớ Student[cite: 5, 7]
            Semester newSemester = new Semester(year, term);
            student.addSemester(newSemester);

            // 2. Lưu vĩnh viễn vào file JSON thông qua DataManager[cite: 6]
            DataManager.saveStudent(student);

            // 3. Cập nhật giao diện (Thêm thẻ mới lên đầu)[cite: 7]
            HBox newCard = createCard(newSemester);
            semesterListContainer.getChildren().add(0, newCard);

            // 4. Reset và ẩn form
            hideAddForm();
            System.out.println("✅ Semester added and saved successfully!");

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid year format!");
        } catch (IOException e) {
            System.err.println("❌ Error saving to student_data.json!");
            e.printStackTrace();
        }
    }
    private void loadCards() {
        if (semesterListContainer == null) return;
        semesterListContainer.getChildren().clear();

        if (student != null && student.getSemesters() != null) {
            // Hiển thị từ mới nhất đến cũ nhất
            for (int i = student.getSemesters().size() - 1; i >= 0; i--) {
                HBox card = createCard(student.getSemesters().get(i));
                semesterListContainer.getChildren().add(card);
            }
        }
    }

    private HBox createCard(Semester semester) {
        HBox card = new HBox();

        // ADDED BEAUTIFUL DROP SHADOW AND ROUNDED CORNERS HERE
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 12; " +
                "-fx-border-radius: 12; " +
                "-fx-border-color: #e5e7eb; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4);");

        card.setPadding(new Insets(20, 25, 20, 25));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefHeight(100);

        // Left side
        VBox leftInfo = new VBox(3);
        leftInfo.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(semester.getDisplayName());
        nameLabel.setStyle("-fx-font-size: 19px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        String subText = semester.getTermType() + " " + semester.getYear();
        Label termLabel = new Label(subText);
        termLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 14px;");

        int courseCount = (semester.getCourses() != null) ? semester.getCourses().size() : 0;
        Label coursesLabel = new Label(courseCount + " courses");
        coursesLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 13px;");

        leftInfo.getChildren().addAll(nameLabel, termLabel, coursesLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right side
        VBox rightInfo = new VBox(4);
        rightInfo.setAlignment(Pos.CENTER_RIGHT);

        Label gpaTitle = new Label("Term GPA");
        gpaTitle.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        double gpa = semester.getGPA();
        Label gpaValue = new Label(gpa == 0.0 ? "N/A" : String.format("%.2f", gpa));
        gpaValue.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        // Build the "Set as Active" button with conditional styling
        Button setBtn = new Button();
        setBtn.setFont(javafx.scene.text.Font.font("System", 12)); // optional

        // Base styles
        String defaultStyle = "-fx-background-color: #e0e7ff; -fx-text-fill: #3730a3; -fx-background-radius: 15; " +
                "-fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 5 15 5 15;";
        String activeStyle  = "-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-background-radius: 15; " +
                "-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 5 15 5 15;";

        if (semester.getDisplayName().equals(student.getActiveSemesterName())) {
            setBtn.setText("Active");
            setBtn.setStyle(activeStyle);
            setBtn.setDisable(true);
        } else {
            setBtn.setText("Set as Active");
            setBtn.setStyle(defaultStyle);
            setBtn.setOnAction(e -> setActiveSemester(semester));

            // Simple hover (better: use CSS pseudo-classes, but this works)
            setBtn.setOnMouseEntered(e -> setBtn.setStyle(
                    "-fx-background-color: #c7d2fe; -fx-text-fill: #3730a3; -fx-background-radius: 15; " +
                            "-fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 5 15 5 15;"));
            setBtn.setOnMouseExited(e -> setBtn.setStyle(defaultStyle));
        }

        rightInfo.getChildren().addAll(gpaTitle, gpaValue, setBtn);

        // Always add all parts to the card
        card.getChildren().addAll(leftInfo, spacer, rightInfo);
        return card;
    }
    private void setActiveSemester(Semester sem) {
        // Update the model
        student.setActiveSemesterName(sem.getDisplayName());

        // Save immediately (so the change persists)
        try {
            DataManager.saveStudent(student);
        } catch (IOException e) {
            System.err.println("❌ Failed to save active semester: " + e.getMessage());
            e.printStackTrace();
        }

        // Rebuild the card list to instantly show which one is active
        loadCards();
        
        // 3. ✅ Trigger the callback to tell Dashboard to refresh the Header!
        if (onActiveSemesterChanged != null) {
            onActiveSemesterChanged.run();
        }
    }
}