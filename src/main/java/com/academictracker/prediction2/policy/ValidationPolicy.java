package com.academictracker.prediction2.policy;

import com.academictracker.prediction2.exception.PredictionInputException;
import com.academictracker.prediction2.model.AssessmentInput;
import com.academictracker.prediction2.model.CourseInput;
import com.academictracker.prediction2.model.TranscriptInput;

/**
 * Small validation policy shared by all simplified engines.
 *
 * <p>It deliberately rejects NaN and Infinity before calculation so invalid
 * values never reach grade-band or GPA formulas.</p>
 */
public final class ValidationPolicy {
    private ValidationPolicy() {
    }

    public static void requireCourse(CourseInput course) {
        if (course == null) {
            throw new PredictionInputException("Course input is required.");
        }
        requireCredits(course.credits(), "course credits");

        double totalWeight = 0.0;
        for (AssessmentInput assessment : course.assessments()) {
            if (assessment == null) {
                throw new PredictionInputException("Assessment input cannot be null.");
            }
            requireWeightPercent(assessment.weightPercent(), "assessment weight");
            if (assessment.score() != null) {
                requireScore(assessment.score(), "assessment score");
            }
            totalWeight += assessment.weightPercent();
        }

        if (totalWeight > 100.0 + GradeScale.WEIGHT_TOLERANCE) {
            throw new PredictionInputException("Total assessment weight cannot exceed 100%.");
        }
    }

    public static void requireTranscript(TranscriptInput transcript) {
        if (transcript == null) {
            throw new PredictionInputException("Transcript input is required.");
        }
        transcript.allCourses().forEach(ValidationPolicy::requireCourse);
    }

    public static void requireTargetScore(double targetScore) {
        requireScore(targetScore, "target score");
    }

    public static void requireTargetGpa(double targetGpa) {
        if (!Double.isFinite(targetGpa) || targetGpa < 0.0 || targetGpa > GradeScale.MAX_GPA) {
            throw new PredictionInputException("Target GPA must be a finite number between 0.0 and 4.0.");
        }
    }

    public static void requireRemainingCredits(int remainingCredits) {
        requireCredits(remainingCredits, "remaining credits");
    }

    public static void requireCredits(int credits, String label) {
        if (credits <= 0) {
            throw new PredictionInputException(label + " must be greater than 0.");
        }
    }

    public static void requireScore(double score, String label) {
        if (!Double.isFinite(score) || score < GradeScale.MIN_SCORE || score > GradeScale.MAX_SCORE) {
            throw new PredictionInputException(label + " must be a finite number between 0 and 100.");
        }
    }

    public static void requireWeightPercent(double weightPercent, String label) {
        if (!Double.isFinite(weightPercent) || weightPercent < 0.0 || weightPercent > 100.0) {
            throw new PredictionInputException(label + " must be a finite number between 0 and 100.");
        }
    }
}
