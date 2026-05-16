package com.academictracker.prediction2;

import com.academictracker.prediction2.engine.TargetGpaEngine;
import com.academictracker.prediction2.exception.PredictionInputException;
import com.academictracker.prediction2.model.AssessmentInput;
import com.academictracker.prediction2.model.CourseInput;
import com.academictracker.prediction2.model.CourseStatus;
import com.academictracker.prediction2.model.SemesterInput;
import com.academictracker.prediction2.model.TranscriptInput;
import com.academictracker.prediction2.result.PredictionStatus;
import com.academictracker.prediction2.result.TargetGpaResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TargetGpaEngineTest {
    private final TargetGpaEngine engine = new TargetGpaEngine();

    @Test
    void targetAlreadyReachedReturnsAlreadyAchieved() {
        TranscriptInput transcript = transcriptWithCompletedCourse(90.0, 3, 12);

        TargetGpaResult result = engine.calculateRequiredFutureGpa(transcript, 3.5);

        assertEquals(PredictionStatus.ALREADY_ACHIEVED, result.status());
        assertEquals(0.0, result.requiredFutureGpa(), 0.0001);
    }

    @Test
    void requiredFutureGpaAtOrBelowFourIsAchievable() {
        TranscriptInput transcript = transcriptWithCompletedCourse(70.0, 3, 3);

        TargetGpaResult result = engine.calculateRequiredFutureGpa(transcript, 3.5);

        assertEquals(PredictionStatus.ACHIEVABLE, result.status());
        assertEquals(4.0, result.requiredFutureGpa(), 0.0001);
    }

    @Test
    void requiredFutureGpaAboveFourReturnsImpossible() {
        TranscriptInput transcript = transcriptWithCompletedCourse(70.0, 30, 3);

        TargetGpaResult result = engine.calculateRequiredFutureGpa(transcript, 4.0);

        assertEquals(PredictionStatus.IMPOSSIBLE, result.status());
    }

    @Test
    void invalidTargetGpaAndCreditsAreRejected() {
        TranscriptInput validTranscript = transcriptWithCompletedCourse(90.0, 3, 12);
        TranscriptInput invalidCredits = transcriptWithCompletedCourse(90.0, 3, 0);

        assertThrows(PredictionInputException.class,
                () -> engine.calculateRequiredFutureGpa(validTranscript, Double.NaN));
        assertThrows(PredictionInputException.class,
                () -> engine.calculateRequiredFutureGpa(validTranscript, 4.01));
        assertThrows(PredictionInputException.class,
                () -> engine.calculateRequiredFutureGpa(invalidCredits, 3.0));
    }

    private TranscriptInput transcriptWithCompletedCourse(double score, int completedCredits, int remainingCredits) {
        CourseInput course = new CourseInput(
                "CORE101",
                "Core Course",
                completedCredits,
                CourseStatus.COMPLETED,
                List.of(new AssessmentInput("Final", 100.0, score))
        );
        return new TranscriptInput(List.of(new SemesterInput("Completed", List.of(course))), remainingCredits);
    }
}
