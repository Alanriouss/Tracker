package com.academictracker.prediction2.result;

/**
 * Result of asking "what do I need on the remaining course work?"
 */
public record CoursePredictionResult(
        String courseCode,
        String courseName,
        double targetScore,
        double currentPaceScore,
        double earnedWeightedScore,
        double remainingWeightPercent,
        double requiredRemainingAverage,
        PredictionStatus status,
        String explanation
) {
}
