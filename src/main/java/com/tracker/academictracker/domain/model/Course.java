package com.tracker.academictracker.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course in the academic tracker.
 * Holds course metadata, status, and a list of assessments (exams, assignments, etc.).
 */
public class Course {
    private String courseCode;              
    private String courseName;               
    private int credits;                     
    private CourseStatus status;             
    private List<Assessment> assessments;

    public Course() {
        this.assessments = new ArrayList<>(); // Avoi NullPointerException when Gson reads the file or when a new course is created from a form
    }

    public Course(String courseCode, String courseName, int credits) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.status = CourseStatus.ACTIVE;   // New courses default to ACTIVE
        this.assessments = new ArrayList<>();
    }
    // ------------------ Getters and Setters ------------------
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public List<Assessment> getAssessments() {
        return assessments;
    }

    // ------------------ Assessment Management ------------------

    public void addAssessment(Assessment assessment) {
        if (assessment != null) {
            this.assessments.add(assessment);
        }
    }

    public void removeAssessment(Assessment assessment) {
        this.assessments.remove(assessment);
    }

    // ------------------ Grade & Weight Calculations ------------------

    /**
     * Calculates the total weight of all assessments in the course.
     *
     * @return Sum of all assessment weights (should ideally be 100%).
     */
    public double getTotalWeight() {
        double totalWeight = 0;
        for (Assessment a : assessments) {
            totalWeight += a.getWeight();
        }
        return totalWeight;
    }

    /**
     * Checks whether the total weight of all assessments equals 100% (within a tiny epsilon for floating‑point errors).
     *
     * @return true if total weight ≈ 100.0, false otherwise.
     */
    public boolean isWeightTotalValid() {
        return Math.abs(getTotalWeight() - 100.0) < 0.001;
    }

    /**
     * Computes the current course grade based on only the assessments that have been graded.
     * The formula: (sum of weighted scores of graded assessments) / (sum of weights of graded assessments) * 100.
     * If no assessments are graded, returns 0.0.
     *
     * A tiny epsilon (1e-9) is added before rounding to fix Java floating‑point inaccuracies
     * (e.g., 56.49999999999999 becomes 56.5000000009, which rounds correctly to 57).
     *
     * @return The current grade percentage rounded to the nearest integer.
     */
    public double getCurrentGrade() {
        double earnedPoints = 0;
        double gradedWeightTotal = 0;
        for (Assessment a : assessments) {
            if (a.isGraded()) {
                earnedPoints += a.getWeightScore();
                gradedWeightTotal += a.getWeight();
            }
        }
        if (gradedWeightTotal == 0) {
            return 0.0;
        }
        double rawGrade = (earnedPoints / gradedWeightTotal) * 100;
        return Math.round(rawGrade + 1e-9);
    }
}