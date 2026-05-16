package com.tracker.academictracker.ui.controller;

import com.tracker.academictracker.domain.model.Student;
import com.tracker.academictracker.persistence.DataManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SettingsController {

    private Student student;

    public void initData(Student student) {
        this.student = student;
    }

    @FXML
    private void handleSaveData() {
        try {
            DataManager.saveStudent(student);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Data saved successfully to your local drive!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save data: " + e.getMessage());
        }
    }

    @FXML
    private void handleLoadData() {
        try {
            Student loaded = DataManager.loadStudent();
            if (loaded != null) {
                // To keep things simple, we just alert the user.
                // A full refresh would require passing the new student back to the Main app.
                showAlert(Alert.AlertType.INFORMATION, "Success", "Data loaded from disk! Please restart the app to see all changes.");
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load data.");
        }
    }

    @FXML
    private void handleExportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Data File");
        fileChooser.setInitialFileName("academic_tracker_backup.json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                // Copy our working save file to the user's chosen location
                File currentSave = new File("student_data.json");
                if (currentSave.exists()) {
                    Files.copy(currentSave.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    showAlert(Alert.AlertType.INFORMATION, "Export Successful", "Your data was exported to:\n" + file.getAbsolutePath());
                } else {
                    showAlert(Alert.AlertType.WARNING, "Warning", "No save data found to export yet. Try saving first.");
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Export Failed", e.getMessage());
            }
        }
    }

    @FXML
    private void handleImportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Data File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                // Overwrite our working save file with the imported one
                File currentSave = new File("student_data.json");
                Files.copy(file.toPath(), currentSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                showAlert(Alert.AlertType.INFORMATION, "Import Successful", "Data imported successfully! Please restart the application to load the new data.");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Import Failed", e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}