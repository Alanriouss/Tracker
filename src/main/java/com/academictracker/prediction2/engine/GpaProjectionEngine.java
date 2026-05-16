package com.academictracker.prediction2.engine;

import com.academictracker.prediction2.model.CourseInput;
import com.academictracker.prediction2.model.CourseStatus;
import com.academictracker.prediction2.model.TranscriptInput;
import com.academictracker.prediction2.policy.GradeScale;
import com.academictracker.prediction2.policy.ValidationPolicy;
import com.academictracker.prediction2.result.GpaProjectionResult;
import com.academictracker.prediction2.result.GradeResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates official GPA separately from projected GPA.
 *
 * <p>Official GPA uses completed and officially complete courses only.
 * Projected GPA includes official completed courses plus active courses that
 * already have graded assessment evidence.</p>
 */
public class GpaProjectionEngine {
    private final CourseGradeEngine courseGradeEngine;
    private final GradeScale gradeScale;

    public GpaProjectionEngine() {
        this(new CourseGradeEngine(), new GradeScale());
    }

    public GpaProjectionEngine(CourseGradeEngine courseGradeEngine, GradeScale gradeScale) {
        this.courseGradeEngine = courseGradeEngine;
        this.gradeScale = gradeScale;
    }

    public GpaProjectionResult project(TranscriptInput transcript) {
        ValidationPolicy.requireTranscript(transcript);

        double officialPoints = 0.0;
        int officialCredits = 0;
        double projectedPoints = 0.0;
        int projectedCredits = 0;
        int activeCreditsIncluded = 0;
        int droppedCoursesExcluded = 0;
        List<String> warnings = new ArrayList<>();

        for (CourseInput course : transcript.allCourses()) {
            if (course.status() == CourseStatus.DROPPED) {
                droppedCoursesExcluded++;
                continue;
            }

            GradeResult courseGrade = courseGradeEngine.calculateCurrentGrade(course);

            if (course.status() == CourseStatus.COMPLETED) {
                if (courseGradeEngine.isOfficiallyComplete(course)) {
                    double finalScore = courseGrade.earnedWeightedScore();
                    double gpa = gradeScale.convert(finalScore).gpaScale();
                    officialPoints += gpa * course.credits();
                    officialCredits += course.credits();
                    projectedPoints += gpa * course.credits();
                    projectedCredits += course.credits();
                } else {
                    warnings.add("Completed course " + course.courseCode()
                            + " is not official because assessment scores or weights are incomplete.");
                }
                continue;
            }

            if (course.status() == CourseStatus.ACTIVE) {
                if (courseGrade.gradedWeightPercent() <= GradeScale.WEIGHT_TOLERANCE) {
                    warnings.add("Active course " + course.courseCode()
                            + " is excluded from projected GPA because it has no graded assessments yet.");
                    continue;
                }
                projectedPoints += courseGrade.gpaScale() * course.credits();
                projectedCredits += course.credits();
                activeCreditsIncluded += course.credits();
            }
        }

        double officialGpa = officialCredits == 0 ? 0.0 : officialPoints / officialCredits;
        double projectedGpa = projectedCredits == 0 ? 0.0 : projectedPoints / projectedCredits;

        return new GpaProjectionResult(
                officialGpa,
                projectedGpa,
                officialCredits,
                projectedCredits,
                activeCreditsIncluded,
                droppedCoursesExcluded,
                warnings
        );
    }
}
