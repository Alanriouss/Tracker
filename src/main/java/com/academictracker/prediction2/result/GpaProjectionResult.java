package com.academictracker.prediction2.result;

import java.util.List;

/**
 * Separates official GPA from projected GPA.
 */
public record GpaProjectionResult(
        double officialGpa,
        double projectedGpa,
        int officialCredits,
        int projectedCredits,
        int activeCreditsIncluded,
        int droppedCoursesExcluded,
        List<String> warnings
) {
    public GpaProjectionResult {
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
