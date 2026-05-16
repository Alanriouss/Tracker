package com.tracker.academictracker.persistence;
import com.tracker.academictracker.domain.model.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DataManager {
    private static final String FILE_NAME = "student_data.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    public static void saveStudent(Student student) throws IOException {
        String json = gson.toJson(student);
        Path targetPath = Paths.get(FILE_NAME);
        Path tempPath = Paths.get(FILE_NAME + ".tmp");
        Files.write(tempPath, json.getBytes());
        Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        System.out.println("✅ Data successfully saved to " + FILE_NAME);
    }

    public static Student loadStudent() throws IOException {
        Path tempPath = Paths.get(FILE_NAME + ".tmp");
        Path targetPath = Paths.get(FILE_NAME);
        if (Files.exists(tempPath)) {
            try {
                Files.delete(tempPath);
            } catch (Exception ignored) {
            }
        }
        if (!Files.exists(targetPath)) {
            System.out.println("No existing save file found.");
            return null;
        }

        String json = new String(Files.readAllBytes(targetPath));
        Student loadedStudent = gson.fromJson(json, Student.class);

        if (loadedStudent != null) {
            System.out.println("✅ Data successfully loaded for: " + loadedStudent.getFullName());
        }

        return loadedStudent;
    }
    public static boolean saveFileExists() {
        return Files.exists(Paths.get(FILE_NAME));
    }
}
