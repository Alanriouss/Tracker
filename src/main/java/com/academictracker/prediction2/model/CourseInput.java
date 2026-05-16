package com.academictracker.prediction2.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Input DTO for course-level calculation and GPA projection.
 *
 * <p>The class intentionally keeps only report-friendly fields. It does not know
 * about JavaFX, JSON storage, accounts, or transcript export.</p>
 */
public record CourseInput(
        String courseCode,
        String courseName,
        int credits,
        CourseStatus status,
        List<AssessmentInput> assessments
) {
    public CourseInput {
        courseCode = courseCode == null ? "" : courseCode.trim();
        courseName = courseName == null || courseName.isBlank() ? courseCode : courseName.trim();
        status = status == null ? CourseStatus.ACTIVE : status;
        assessments = assessments == null ? List.of() : List.copyOf(new ArrayList<>(assessments));
    }
}
