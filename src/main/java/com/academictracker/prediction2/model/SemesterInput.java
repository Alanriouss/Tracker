package com.academictracker.prediction2.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Input DTO for grouping courses by academic term.
 */
public record SemesterInput(String name, List<CourseInput> courses) {
    public SemesterInput {
        name = name == null || name.isBlank() ? "Semester" : name.trim();
        courses = courses == null ? List.of() : List.copyOf(new ArrayList<>(courses));
    }
}
