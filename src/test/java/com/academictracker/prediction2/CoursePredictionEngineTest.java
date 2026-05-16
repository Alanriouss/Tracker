package com.academictracker.prediction2;

import com.academictracker.prediction2.engine.CourseGradeEngine;
import com.academictracker.prediction2.engine.CoursePredictionEngine;
import com.academictracker.prediction2.exception.PredictionInputException;
import com.academictracker.prediction2.model.AssessmentInput;
import com.academictracker.prediction2.model.CourseInput;
import com.academictracker.prediction2.model.CourseStatus;
import com.academictracker.prediction2.policy.GradeScale;
import com.academictracker.prediction2.result.CoursePredictionResult;
import com.academictracker.prediction2.result.GradeResult;
import com.academictracker.prediction2.result.PredictionStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CoursePredictionEngineTest {
    private final GradeScale gradeScale = new GradeScale();
    private final CourseGradeEngine gradeEngine = new CourseGradeEngine();
    private final CoursePredictionEngine predictionEngine = new CoursePredictionEngine();

    @Test
    void gradeScaleConvertsBoundaryScores() {
        assertGrade(100.0, "A+", 4.0, true);
        assertGrade(90.0, "A+", 4.0, true);
        assertGrade(89.99, "A", 3.5, true);
        assertGrade(80.0, "A", 3.5, true);
        assertGrade(70.0, "B+", 3.0, true);
        assertGrade(60.0, "B", 2.5, true);
        assertGrade(50.0, "C", 2.0, true);
        assertGrade(49.99, "D+", 1.5, false);
        assertGrade(40.0, "D+", 1.5, false);
        assertGrade(30.0, "D", 1.0, false);
        assertGrade(29.99, "F", 0.0, false);
    }

    @Test
    void missingScoresAreNotCountedAsZero() {
        CourseInput course = new CourseInput(
                "STAT101",
                "Statistics",
                3,
                CourseStatus.ACTIVE,
                List.of(
                        new AssessmentInput("Midterm", 50.0, 80.0),
                        new AssessmentInput("Final", 50.0, null)
                )
        );

        GradeResult result = gradeEngine.calculateCurrentGrade(course);

        assertEquals(80.0, result.score100(), 0.0001);
        assertEquals(40.0, result.earnedWeightedScore(), 0.0001);
        assertEquals(50.0, result.gradedWeightPercent(), 0.0001);
        assertEquals(50.0, result.remainingWeightPercent(), 0.0001);
        assertEquals("A", result.letterGrade());
    }

    @Test
    void targetScoreOutsideZeroToOneHundredIsRejected() {
        CourseInput course = activeCourseWithMissingFinal();

        assertThrows(PredictionInputException.class, () -> predictionEngine.predictTarget(course, -1.0));
        assertThrows(PredictionInputException.class, () -> predictionEngine.predictTarget(course, 100.01));
    }

    @Test
    void requiredRemainingScoreAboveOneHundredReturnsImpossible() {
        CoursePredictionResult result = predictionEngine.predictTarget(activeCourseWithMissingFinal(), 95.0);

        assertEquals(PredictionStatus.IMPOSSIBLE, result.status());
        assertEquals(110.0, result.requiredRemainingAverage(), 0.0001);
    }

    @Test
    void alreadyAchievedTargetReturnsAlreadyAchieved() {
        CourseInput course = new CourseInput(
                "JAVA201",
                "Java",
                3,
                CourseStatus.ACTIVE,
                List.of(
                        new AssessmentInput("Project", 80.0, 100.0),
                        new AssessmentInput("Final", 20.0, null)
                )
        );

        CoursePredictionResult result = predictionEngine.predictTarget(course, 75.0);

        assertEquals(PredictionStatus.ALREADY_ACHIEVED, result.status());
        assertEquals(0.0, result.requiredRemainingAverage(), 0.0001);
    }

    private void assertGrade(double score, String letter, double scale4, boolean passing) {
        GradeResult result = gradeScale.convert(score);
        assertEquals(letter, result.letterGrade());
        assertEquals(scale4, result.gpaScale(), 0.0001);
        assertEquals(passing, result.passing());
    }

    private CourseInput activeCourseWithMissingFinal() {
        return new CourseInput(
                "MATH101",
                "Calculus",
                3,
                CourseStatus.ACTIVE,
                List.of(
                        new AssessmentInput("Midterm", 50.0, 80.0),
                        new AssessmentInput("Final", 50.0, null)
                )
        );
    }
}
