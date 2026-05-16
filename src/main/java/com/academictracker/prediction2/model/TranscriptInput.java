package com.academictracker.prediction2.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Root input DTO for GPA projection and target-GPA calculation.
 *
 * @param semesters historical and current course groups
 * @param remainingCredits future credits still available for target GPA planning
 */
public record TranscriptInput(List<SemesterInput> semesters, int remainingCredits) {
    public TranscriptInput {
        semesters = semesters == null ? List.of() : List.copyOf(new ArrayList<>(semesters));
    }

    /**
     * Flattens semesters into courses so engines can stay simple and UML-friendly.
     */
    public List<CourseInput> allCourses() {
        return semesters.stream()
                .flatMap(semester -> semester.courses().stream())
                .toList();
    }
}
