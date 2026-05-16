package com.academictracker.prediction2.engine;

import com.academictracker.prediction2.model.AssessmentInput;
import com.academictracker.prediction2.model.CourseInput;
import com.academictracker.prediction2.policy.GradeScale;
import com.academictracker.prediction2.policy.ValidationPolicy;
import com.academictracker.prediction2.result.GradeResult;

/**
 * Calculates the current grade state of one course.
 *
 * <p>Important rule: missing scores are not treated as zero. The engine only
 * uses graded assessments for the current earned score and current pace.
 * Ungraded assessments simply reduce the known portion of the course,
 * so the "current pace" reflects performance on what has been evaluated so far.
 * </p>
 */
public class CourseGradeEngine {
    // Responsible for converting a numeric score into a letter grade
    private final GradeScale gradeScale;
    public CourseGradeEngine() {
        this(new GradeScale());
    }

    /**
     * Constructor that allows a custom grading scale (e.g., different letter mappings).
     *
     * @param gradeScale the grade scale to use for score-to-letter conversion
     */
    public CourseGradeEngine(GradeScale gradeScale) {
        this.gradeScale = gradeScale;
    }

    /**
     * Calculates the current grade for a course based solely on the assessments
     * that have already been graded (score != null).
     *
     * <p>How it works:
     * <ul>
     *   <li>Sum the total weight of all assessments (should be 100%).</li>
     *   <li>For graded assessments, accumulate the weighted score:
     *       {@code (score * weight) / 100}.</li>
     *   <li>Track the total weight of those graded assessments.</li>
     *   <li>The current pace (or "what‑if" grade) is computed as:
     *       {@code earnedWeightedScore / gradedWeight * 100}.
     *       If nothing is graded yet, it returns 0.0.</li>
     *   <li>The remaining weight is {@code 100 - gradedWeight}.</li>
     * </ul>
     *
     * @param course the course input containing assessments (some graded, some not)
     * @return a {@link GradeResult} with the current pace score, earned points, and grade breakdown
     */
    public GradeResult calculateCurrentGrade(CourseInput course) {
        // Ensure the course object is valid (not null, contains assessments, etc.)
        ValidationPolicy.requireCourse(course);

        double totalWeight = 0.0;          // total weight of all assessments (should be 100)
        double gradedWeight = 0.0;        // weight of assessments that have a score
        double earnedWeightedScore = 0.0; // sum of (score * weight / 100) for graded items

        for (AssessmentInput assessment : course.assessments()) {
            totalWeight += assessment.weightPercent();
            if (assessment.isGraded()) {
                gradedWeight += assessment.weightPercent();
                // Convert percentage score to weighted contribution:
                // Example: score=85, weight=30% → 85 * 30 / 100 = 25.5 weighted points
                earnedWeightedScore += assessment.score() * assessment.weightPercent() / 100.0;
            }
        }

        // How much of the course weight is still unknown (ungraded)
        double remainingWeight = Math.max(0.0, 100.0 - gradedWeight);

        // Current pace = what the student would have if all graded work were scaled to 100%
        double currentPaceScore = gradedWeight > GradeScale.WEIGHT_TOLERANCE
                ? earnedWeightedScore / gradedWeight * 100.0
                : 0.0;

        // Delegate to the grade scale to produce a letter grade (and possibly other data)
        return gradeScale.convert(
                currentPaceScore,
                earnedWeightedScore,
                gradedWeight,
                remainingWeight
        );
    }

    /**
     * Determines whether a course is officially complete, i.e., every assessment
     * has been graded and the total weight of all assessments sums to 100%
     * (within {@link GradeScale#WEIGHT_TOLERANCE}).
     *
     * @param course the course to check
     * @return {@code true} if all assessments are graded and total weight ≈ 100%
     */
    public boolean isOfficiallyComplete(CourseInput course) {
        ValidationPolicy.requireCourse(course);

        // Total weight of all assessments (ideally 100)
        double totalWeight = course.assessments().stream()
                .mapToDouble(AssessmentInput::weightPercent)
                .sum();

        // Check that every assessment has a score
        boolean allScoresPresent = course.assessments().stream()
                .allMatch(AssessmentInput::isGraded);

        // A course is complete only if it has at least one assessment,
        // the total weight equals 100 (within tolerance), and every item is graded.
        return !course.assessments().isEmpty()
                && Math.abs(totalWeight - 100.0) <= GradeScale.WEIGHT_TOLERANCE
                && allScoresPresent;
    }
}