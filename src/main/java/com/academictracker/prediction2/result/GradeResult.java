package com.academictracker.prediction2.result;

/**
 * Grade conversion result plus course-weight context.
 *
 * @param score100 score used for letter/GPA conversion; for in-progress courses this is current pace
 * @param letterGrade letter grade from the configured grade scale
 * @param gpaScale GPA value on the 4-point scale
 * @param classification human-readable classification
 * @param passing true when the score passes the course
 * @param earnedWeightedScore current contribution to final course score
 * @param gradedWeightPercent total graded assessment weight
 * @param remainingWeightPercent future/ungraded/unassigned assessment weight
 */
public record GradeResult(
        double score100,
        String letterGrade,
        double gpaScale,
        String classification,
        boolean passing,
        double earnedWeightedScore,
        double gradedWeightPercent,
        double remainingWeightPercent
) {
}
