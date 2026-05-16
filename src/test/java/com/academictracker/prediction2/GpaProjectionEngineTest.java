package com.academictracker.prediction2;

import com.academictracker.prediction2.engine.GpaProjectionEngine;
import com.academictracker.prediction2.model.AssessmentInput;
import com.academictracker.prediction2.model.CourseInput;
import com.academictracker.prediction2.model.CourseStatus;
import com.academictracker.prediction2.model.SemesterInput;
import com.academictracker.prediction2.model.TranscriptInput;
import com.academictracker.prediction2.result.GpaProjectionResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GpaProjectionEngineTest {
    private final GpaProjectionEngine engine = new GpaProjectionEngine();

    @Test
    void droppedCoursesAreExcludedFromGpa() {
        TranscriptInput transcript = new TranscriptInput(
                List.of(new SemesterInput("Year 1", List.of(
                        completedCourse("HIST101", "History", 3, 90.0),
                        new CourseInput("DROP101", "Dropped Course", 4, CourseStatus.DROPPED,
                                List.of(new AssessmentInput("Final", 100.0, 0.0)))
                ))),
                30
        );

        GpaProjectionResult result = engine.project(transcript);

        assertEquals(4.0, result.officialGpa(), 0.0001);
        assertEquals(3, result.officialCredits());
        assertEquals(1, result.droppedCoursesExcluded());
    }

    @Test
    void activeCoursesAreSeparatedFromOfficialGpaAndIncludedInProjectedGpa() {
        TranscriptInput transcript = new TranscriptInput(
                List.of(new SemesterInput("Current", List.of(
                        completedCourse("CS101", "Intro CS", 3, 90.0),
                        new CourseInput("MATH101", "Calculus", 3, CourseStatus.ACTIVE,
                                List.of(
                                        new AssessmentInput("Midterm", 50.0, 80.0),
                                        new AssessmentInput("Final", 50.0, null)
                                ))
                ))),
                30
        );

        GpaProjectionResult result = engine.project(transcript);

        assertEquals(4.0, result.officialGpa(), 0.0001);
        assertEquals(3, result.officialCredits());
        assertEquals(3.75, result.projectedGpa(), 0.0001);
        assertEquals(6, result.projectedCredits());
        assertEquals(3, result.activeCreditsIncluded());
        assertFalse(result.warnings().contains("Dropped"));
    }

    private CourseInput completedCourse(String code, String name, int credits, double score) {
        return new CourseInput(
                code,
                name,
                credits,
                CourseStatus.COMPLETED,
                List.of(new AssessmentInput("Final", 100.0, score))
        );
    }
}
