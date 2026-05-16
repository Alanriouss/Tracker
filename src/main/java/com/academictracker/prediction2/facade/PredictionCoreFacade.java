package com.academictracker.prediction2.facade;

import com.academictracker.prediction2.engine.CourseGradeEngine;
import com.academictracker.prediction2.engine.CoursePredictionEngine;
import com.academictracker.prediction2.engine.GpaProjectionEngine;
import com.academictracker.prediction2.engine.TargetGpaEngine;
import com.academictracker.prediction2.model.CourseInput;
import com.academictracker.prediction2.model.TranscriptInput;
import com.academictracker.prediction2.result.CoursePredictionResult;
import com.academictracker.prediction2.result.GpaProjectionResult;
import com.academictracker.prediction2.result.GradeResult;
import com.academictracker.prediction2.result.TargetGpaResult;

/**
 * Simple public API for report/UML usage.
 *
 * <p>External callers can use this facade instead of constructing individual
 * engines. The original JavaFX app is not wired to this class; this is a clean,
 * documentation-focused prediction core.</p>
 */
public class PredictionCoreFacade {
    private final CourseGradeEngine courseGradeEngine;
    private final CoursePredictionEngine coursePredictionEngine;
    private final GpaProjectionEngine gpaProjectionEngine;
    private final TargetGpaEngine targetGpaEngine;

    public PredictionCoreFacade() {
        this.courseGradeEngine = new CourseGradeEngine();
        this.coursePredictionEngine = new CoursePredictionEngine(courseGradeEngine);
        this.gpaProjectionEngine = new GpaProjectionEngine();
        this.targetGpaEngine = new TargetGpaEngine(gpaProjectionEngine);
    }

    public GradeResult calculateCourseGrade(CourseInput course) {
        return courseGradeEngine.calculateCurrentGrade(course);
    }

    public CoursePredictionResult predictCourseTarget(CourseInput course, double targetScore) {
        return coursePredictionEngine.predictTarget(course, targetScore);
    }

    public GpaProjectionResult projectGpa(TranscriptInput transcript) {
        return gpaProjectionEngine.project(transcript);
    }

    public TargetGpaResult calculateRequiredFutureGpa(TranscriptInput transcript, double targetGpa) {
        return targetGpaEngine.calculateRequiredFutureGpa(transcript, targetGpa);
    }
}
