package com.academictracker.prediction2.result;

/**
 * Result of calculating the future GPA required to reach a cumulative target.
 */
public record TargetGpaResult(
        double currentOfficialGpa,
        double targetGpa,
        int completedCredits,
        int remainingCredits,
        double requiredFutureGpa,
        PredictionStatus status,
        String explanation
) {
}
