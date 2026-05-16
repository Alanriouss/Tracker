package com.tracker.academictracker.domain.model;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a semester (e.g., "2025 - Fall") containing a list of courses.
 * Provides methods to compute GPA and average score for the semester.
 */
public class Semester {
    private int year;
    private String termType;
    private List<Course> courses;   

    public Semester() {
        this.courses = new ArrayList<>();
    }
    public Semester(int year, String termType) {
        this.year = year;
        this.termType = termType;
        this.courses = new ArrayList<>();
    }
    // ------------------ Getters and Setters ------------------
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public List<Course> getCourses() {
        return this.courses;
    }

    // ------------------ Course Management ------------------

    public void addCourse(Course course) {
        this.courses.add(course);
    }
    public void removeCourse(Course course) {
        this.courses.remove(course);
    }

    /**
     * Returns a user-friendly display name, e.g., "2025 - Fall".
     */
    public String getDisplayName() {
        return year + " - " + termType;
    }
    // ------------------ GPA & Score Calculations ------------------

    /**
     * Calculates the semester GPA (Grade Point Average) on a 4.0 scale.
     * Only courses with status != DROPPED are considered.
     * The GPA is credit-weighted.
     *
     * @return The GPA, or 0.0 if no valid courses exist.
     */
    public double getGPA() {
        if (courses == null || courses.isEmpty()) return 0.0;

        double totalPoints = 0;
        int totalCredits = 0;

        for (Course c : courses) {
            // Skip dropped courses – they don't count toward GPA
            if (c.getStatus() == CourseStatus.DROPPED) continue;

            double gpaPoints = convertToGPAPoints(c.getCurrentGrade());
            totalPoints += gpaPoints * c.getCredits();
            totalCredits += c.getCredits();
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    /**
     * Calculates the average GPA points for all active and completed courses
     * that have at least one graded assessment.  Dropped courses and
     * courses with no graded work are excluded.
     *
     * @return Credit-weighted average GPA points, or 0.0 if no applicable courses.
     */
    public double getAverageScore() {
        if (courses == null || courses.isEmpty()) return 0.0;

        double totalScore = 0;
        int totalCredits = 0;

        for (Course c : courses) {
            // Ignore dropped courses
            if (c.getStatus() == CourseStatus.DROPPED) continue;
            // Ignore active courses that have no graded assessments (no data to compute)
            if (c.getStatus() == CourseStatus.ACTIVE &&
                c.getAssessments().stream().noneMatch(Assessment::isGraded)) continue;

            double gpaPoints = convertToGPAPoints(c.getCurrentGrade());
            totalScore += gpaPoints * c.getCredits();
            totalCredits += c.getCredits();
        }
        return totalCredits == 0 ? 0.0 : totalScore / totalCredits;
    }

    /**
     * Converts a percentage grade to a 4.0 GPA point.
     *
     * @param grade The percentage grade (0–100).
     * @return The corresponding GPA value (0.0 – 4.0).
     */
    private double convertToGPAPoints(double grade) {
        if (grade >= 90) return 4.0;   
        if (grade >= 80) return 3.5;   
        if (grade >= 70) return 3.0;   
        if (grade >= 60) return 2.5;   
        if (grade >= 50) return 2.0;  
        if (grade >= 40) return 1.5;   
        if (grade >= 30) return 1.0;   
        return 0.0;                   
    }
}