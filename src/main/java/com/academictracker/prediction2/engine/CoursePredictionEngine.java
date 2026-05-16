package com.academictracker.prediction2.engine;

import com.academictracker.prediction2.model.CourseInput;
import com.academictracker.prediction2.policy.GradeScale;
import com.academictracker.prediction2.policy.ValidationPolicy;
import com.academictracker.prediction2.result.CoursePredictionResult;
import com.academictracker.prediction2.result.GradeResult;
import com.academictracker.prediction2.result.PredictionStatus;

/**
 * Predicts the average score needed on remaining course weight.
 */
public class CoursePredictionEngine {
    private final CourseGradeEngine courseGradeEngine;

    public CoursePredictionEngine() {
        this(new CourseGradeEngine());
    }

    public CoursePredictionEngine(CourseGradeEngine courseGradeEngine) {
        this.courseGradeEngine = courseGradeEngine;
    }

    public CoursePredictionResult predictTarget(CourseInput course, double targetScore) {
        ValidationPolicy.requireCourse(course);
        ValidationPolicy.requireTargetScore(targetScore);

        GradeResult grade = courseGradeEngine.calculateCurrentGrade(course);
        double earned = grade.earnedWeightedScore();
        double remainingWeight = grade.remainingWeightPercent();

        if (earned + GradeScale.WEIGHT_TOLERANCE >= targetScore) {
            return new CoursePredictionResult(
                    course.courseCode(),
                    course.courseName(),
                    targetScore,
                    grade.score100(),
                    earned,
                    remainingWeight,
                    0.0,
                    PredictionStatus.ALREADY_ACHIEVED,
                    "Current earned weighted score already reaches the target even before remaining assessments."
            );
        }

        if (remainingWeight <= GradeScale.WEIGHT_TOLERANCE) {
            return impossible(course, targetScore, grade, 101.0,
                    "No remaining assessment weight is available, so the target cannot be recovered.");
        }

        double requiredRemainingAverage = (targetScore - earned) / (remainingWeight / 100.0);
        if (requiredRemainingAverage > 100.0 + GradeScale.WEIGHT_TOLERANCE) {
            return impossible(course, targetScore, grade, requiredRemainingAverage,
                    "The required remaining average is above 100%, so the target is impossible.");
        }

        return new CoursePredictionResult(
                course.courseCode(),
                course.courseName(),
                targetScore,
                grade.score100(),
                earned,
                remainingWeight,
                Math.max(0.0, requiredRemainingAverage),
                PredictionStatus.ACHIEVABLE,
                "The target is achievable if the remaining assessments average about "
                        + String.format("%.2f", requiredRemainingAverage) + "."
        );
    }

    private CoursePredictionResult impossible(
            CourseInput course,
            double targetScore,
            GradeResult grade,
            double requiredRemainingAverage,
            String explanation
    ) {
        return new CoursePredictionResult(
                course.courseCode(),
                course.courseName(),
                targetScore,
                grade.score100(),
                grade.earnedWeightedScore(),
                grade.remainingWeightPercent(),
                requiredRemainingAverage,
                PredictionStatus.IMPOSSIBLE,
                explanation
        );
    }
}
