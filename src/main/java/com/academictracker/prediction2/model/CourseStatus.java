package com.academictracker.prediction2.model;

/**
 * Minimal course lifecycle used by prediction-core 2.0.
 */
public enum CourseStatus {
    /** Finished course that may contribute to official GPA when all assessments are graded. */
    COMPLETED,

    /** Current course that may contribute only to projected/scenario GPA. */
    ACTIVE,

    /** Course excluded from official and projected GPA calculations. */
    DROPPED
}
