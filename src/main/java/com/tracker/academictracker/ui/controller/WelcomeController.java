package com.tracker.academictracker.ui.controller;

import com.tracker.academictracker.app.Main;
import com.tracker.academictracker.domain.model.Student;
import com.tracker.academictracker.persistence.DataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class WelcomeController {

    @FXML private TextField nameInput;
    @FXML private TextField idInput;
    @FXML private Label errorLabel;

    @FXML
    private void handleCreateProfile(ActionEvent event) {
        String name = nameInput.getText().trim();
        String studentId = idInput.getText().trim();

        // 1. Kiểm tra dữ liệu rỗng
        if (name.isEmpty() || studentId.isEmpty()) {
            errorLabel.setText("❌ Please fill in all fields.");
            return;
        }

        // 2. Tạo Student mới
        Student newStudent = new Student(name, studentId);

        // 3. TODO: Lưu `newStudent` vào file JSON ở đây (Sử dụng Gson sau)
        try {
            DataManager.saveStudent(newStudent);
            System.out.println("Tạo và lưu profile thành công: " + newStudent.getFullName());
        } catch (IOException e) {
            errorLabel.setText("❌ Lỗi khi lưu dữ liệu vào ổ cứng!");
            e.printStackTrace();
            return; // Dừng lại nếu không lưu được
        }

        // 4. Chuyển hướng sang màn hình Dashboard!
        try {
            Main.showDashboard(newStudent);
        } catch (Exception e) {
            errorLabel.setText("❌ Error loading dashboard.");
            e.printStackTrace();
        }
    }
}