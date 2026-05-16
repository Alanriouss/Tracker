package com.academictracker.prediction2.policy;

import com.academictracker.prediction2.result.GradeResult;

/**
 * Central grade-scale policy for prediction-core 2.0.
 *
 * <p>Keeping this conversion in one place makes the UML and report easier to
 * explain: every engine asks this policy to convert a score into a letter grade,
 * GPA points, classification, and pass/no-pass status.</p>
 */
public class GradeScale {

    // ---------- Standard scale constants ----------
    /** Lowest possible score (0%). */
    public static final double MIN_SCORE = 0.0;
    /** Highest possible score (100%). */
    public static final double MAX_SCORE = 100.0;
    /** Maximum GPA on the 4.0 scale. */
    public static final double MAX_GPA = 4.0;
    /** Score needed to pass a course (50%). */
    public static final double PASS_SCORE = 50.0;
    /**
     * Tolerance (dung sai) used when checking that total assessment weight sums to 100%.
     * Floating‑point arithmetic often introduces tiny errors, so we consider
     * a total within ±0.001 of 100 to be valid.
     */
    public static final double WEIGHT_TOLERANCE = 0.001;

    // --------------------------------------------------
    //          Convenience conversion method
    // --------------------------------------------------

    /**
     * Simple conversion for a single absolute score (0–100).
     * Assumes the score already represents the full course grade.
     *
     * @param score100 the percentage score
     * @return a fully populated {@link GradeResult}
     */
    public GradeResult convert(double score100) {
        // Delegates to the full converter with default values (100% graded weight, 0% remaining)
        return convert(score100, score100, 100.0, 0.0);
    }

    // --------------------------------------------------
    //          Full conversion method
    // --------------------------------------------------

    /**
     * Converts a course grade score into a {@link GradeResult} that includes:
     * <ul>
     *   <li>Letter grade (A+, A, B+, …)</li>
     *   <li>GPA points on a 4.0 scale</li>
     *   <li>Classification (Excellent, Very good, …)</li>
     *   <li>Pass / no-pass status</li>
     *   <li>Detailed earning breakdown (weighted scores, graded/remaining weights)</li>
     * </ul>
     *
     * @param score100              the current pace score (0–100)
     * @param earnedWeightedScore   sum of weighted scores of graded items
     * @param gradedWeightPercent   total weight of graded assessments (0–100)
     * @param remainingWeightPercent total weight of ungraded assessments (0–100)
     * @return a fully populated {@link GradeResult}
     */
    public GradeResult convert(
            double score100,
            double earnedWeightedScore,
            double gradedWeightPercent,
            double remainingWeightPercent
    ) {
        // Validate the score range (0–100 inclusive)
        ValidationPolicy.requireScore(score100, "score");

        // ----- Apply grading thresholds (top-down) -----
        // Each threshold maps the score to a letter grade, GPA, classification, and pass status.

        if (score100 >= 90.0) {
            return result(score100, "A+", 4.0, "Excellent", true,
                    earnedWeightedScore, gradedWeightPercent, remainingWeightPercent);
        }
        if (score100 >= 80.0) {
            return result(score100, "A", 3.5, "Very good", true,
                    earnedWeightedScore, gradedWeightPercent, remainingWeightPercent);
        }
        if (score100 >= 70.0) {
            return result(score100, "B+", 3.0, "Good", true,
                    earnedWeightedScore, gradedWeightPercent, remainingWeightPercent);
        }
        if (score100 >= 60.0) {
            return result(score100, "B", 2.5, "Fair", true,
                    earnedWeightedScore, gradedWeightPercent, remainingWeightPercent);
        }
        if (score100 >= 50.0) {
            return result(score100, "C", 2.0, "Average", true,
                    earnedWeightedScore, gradedWeightPercent, remainingWeightPercent);
        }
        if (score100 >= 40.0) {
            return result(score100, "D+", 1.5, "Weak", false,
                    earnedWeightedScore, gradedWeightPercent, remainingWeightPercent);
        }
        if (score100 >= 30.0) {
            return result(score100, "D", 1.0, "Very weak", false,
                    earnedWeightedScore, gradedWeightPercent, remainingWeightPercent);
        }
        // Score below 30 → F (fail)
        return result(score100, "F", 0.0, "No passing", false,
                earnedWeightedScore, gradedWeightPercent, remainingWeightPercent);
    }

    // --------------------------------------------------
    //          Private helper to build GradeResult
    // --------------------------------------------------

    /**
     * Creates a {@link GradeResult} with all required fields.
     * Keeps the constructor calls consistent across all thresholds.
     */
    private GradeResult result(
            double score100,
            String letter,
            double gpaScale,
            String classification,
            boolean passing,
            double earnedWeightedScore,
            double gradedWeightPercent,
            double remainingWeightPercent
    ) {
        return new GradeResult(
                score100,
                letter,
                gpaScale,
                classification,
                passing,
                earnedWeightedScore,
                gradedWeightPercent,
                remainingWeightPercent
        );
    }
}