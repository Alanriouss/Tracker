package com.academictracker.prediction2.model;

/**
 * Input DTO for one assessment component.
 *
 * @param name assessment label, for example Midterm or Final
 * @param weightPercent assessment weight in percent, from 0 to 100
 * @param score score in the 0-100 scale; null means not graded yet
 */
public record AssessmentInput(String name, double weightPercent, Double score) {
    public AssessmentInput {
        name = name == null || name.isBlank() ? "Assessment" : name.trim();
    }

    public boolean isGraded() {
        return score != null;
    }
}
