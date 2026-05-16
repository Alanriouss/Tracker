package com.tracker.academictracker.service;
import com.academictracker.prediction2.facade.PredictionCoreFacade;
import com.academictracker.prediction2.model.SemesterInput;
import com.academictracker.prediction2.model.TranscriptInput;
import com.academictracker.prediction2.result.CoursePredictionResult;
import com.academictracker.prediction2.result.GpaProjectionResult;
import com.academictracker.prediction2.result.GradeResult;
import com.academictracker.prediction2.result.TargetGpaResult;
import com.tracker.academictracker.domain.model.Course;
import com.tracker.academictracker.domain.model.Semester;
import com.tracker.academictracker.domain.model.Student;
import com.tracker.academictracker.mapper.PredictionMapper;

import java.util.List;

public class PredictionService {
    private static final int UNUSED_REMAINING_CREDITS_PLACEHOLDER = 1;

    private final PredictionMapper mapper;
    private final PredictionCoreFacade facade;

    public PredictionService() {
        this(new PredictionMapper(), new PredictionCoreFacade());
    }

    PredictionService(PredictionMapper mapper, PredictionCoreFacade facade) {
        this.mapper = mapper;
        this.facade = facade;
    }

    public GradeResult calculateCourseGrade(Course course) {
        return facade.calculateCourseGrade(mapper.toCourseInput(course));
    }

    public CoursePredictionResult predictCourseTarget(Course course, double targetScore) {
        return facade.predictCourseTarget(mapper.toCourseInput(course), targetScore);
    }

    public GpaProjectionResult projectStudentGpa(Student student) {
        return facade.projectGpa(mapper.toTranscriptInput(student, UNUSED_REMAINING_CREDITS_PLACEHOLDER));
    }

    public GpaProjectionResult projectSemesterGpa(Semester semester) {
        SemesterInput semesterInput = mapper.toSemesterInput(semester);
        TranscriptInput transcript = new TranscriptInput(
                List.of(semesterInput),
                UNUSED_REMAINING_CREDITS_PLACEHOLDER
        );
        return facade.projectGpa(transcript);
    }

    public TargetGpaResult calculateRequiredActiveTermGpa(
            Student student,
            Semester activeSemester,
            double targetGpa
    ) {
        int activeCredits = mapper.countActiveCredits(activeSemester);
        if (activeCredits <= 0) {
            throw new IllegalArgumentException("No active credits available for target GPA calculation.");
        }

        TranscriptInput transcript = mapper.toTranscriptInput(student, activeCredits);
        return facade.calculateRequiredFutureGpa(transcript, targetGpa);
    }
}
