package com.tracker.academictracker.mapper;

import com.academictracker.prediction2.model.AssessmentInput;
import com.academictracker.prediction2.model.CourseInput;
import com.academictracker.prediction2.model.SemesterInput;
import com.academictracker.prediction2.model.TranscriptInput;
import com.tracker.academictracker.domain.model.Assessment;
import com.tracker.academictracker.domain.model.Course;
import com.tracker.academictracker.domain.model.Semester;
import com.tracker.academictracker.domain.model.Student;

import java.util.List;

public class PredictionMapper {

    public TranscriptInput toTranscriptInput(Student student, int remainingCredits) {
        requireNonNull(student, "Student is required for prediction.");
        List<Semester> semesters = student.getSemesters() == null ? List.of() : student.getSemesters();
        return new TranscriptInput(
                semesters.stream()
                        .map(this::toSemesterInput)
                        .toList(),
                remainingCredits
        );
    }

    public SemesterInput toSemesterInput(Semester semester) {
        requireNonNull(semester, "Semester is required for prediction.");
        List<Course> courses = semester.getCourses() == null ? List.of() : semester.getCourses();
        return new SemesterInput(
                semester.getDisplayName(),
                courses.stream()
                        .map(this::toCourseInput)
                        .toList()
        );
    }

    public CourseInput toCourseInput(Course course) {
        requireNonNull(course, "Course is required for prediction.");
        List<Assessment> assessments = course.getAssessments() == null ? List.of() : course.getAssessments();
        return new CourseInput(
                course.getCourseCode(),
                course.getCourseName(),
                course.getCredits(),
                toPredictionStatus(course.getStatus()),
                assessments.stream()
                        .map(this::toAssessmentInput)
                        .toList()
        );
    }

    public AssessmentInput toAssessmentInput(Assessment assessment) {
        requireNonNull(assessment, "Assessment is required for prediction.");
        Double score = assessment.getScore();
        double maxScore = assessment.getMaxScore();
        Double score100 = null;

        if (maxScore <= 0.0) {
            throw new IllegalArgumentException("Assessment max score must be greater than 0 for "
                    + assessment.getAssessmentName() + ".");
        }

        if (score != null) {
            score100 = score / maxScore * 100.0;
        }

        return new AssessmentInput(
                assessment.getAssessmentName(),
                assessment.getWeight(),
                score100
        );
    }

    public int countActiveCredits(Semester semester) {
        requireNonNull(semester, "Semester is required for active credit counting.");
        List<Course> courses = semester.getCourses() == null ? List.of() : semester.getCourses();
        int activeCredits = 0;
        for (Course course : courses) {
            requireNonNull(course, "Course is required for active credit counting.");
            if (toPredictionStatus(course.getStatus()) == com.academictracker.prediction2.model.CourseStatus.ACTIVE) {
                activeCredits += course.getCredits();
            }
        }
        return activeCredits;
    }

    private com.academictracker.prediction2.model.CourseStatus toPredictionStatus(
            com.tracker.academictracker.domain.model.CourseStatus status
    ) {
        if (status == null) {
            return com.academictracker.prediction2.model.CourseStatus.ACTIVE;
        }
        return switch (status) {
            case ACTIVE -> com.academictracker.prediction2.model.CourseStatus.ACTIVE;
            case COMPLETED -> com.academictracker.prediction2.model.CourseStatus.COMPLETED;
            case DROPPED -> com.academictracker.prediction2.model.CourseStatus.DROPPED;
        };
    }

    private void requireNonNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
