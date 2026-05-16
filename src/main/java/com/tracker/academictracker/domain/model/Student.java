package com.tracker.academictracker.domain.model; 
import java.util.ArrayList;
import java.util.List;
/**
 * Represents a student in the academic tracker.
 * Holds personal information, a list of semesters, and methods to calculate
 * cumulative GPA, completed-course GPA, total completed credits, and academic standing.
 */
public class Student {
    private String fullName;             
    private String studentId;           
    private List<Semester> semesters;    
    private String activeSemesterName;   
    public Student() {
        this.semesters = new ArrayList<>();
    }
    public Student(String fullName, String studentId) {
        this.fullName = fullName;
        this.studentId = studentId;
        this.semesters = new ArrayList<>();
    }
    // ------------------ Getters and Setters ------------------
    public String getActiveSemesterName() {
        return activeSemesterName;
    }

    public void setActiveSemesterName(String activeSemesterName) {
        this.activeSemesterName = activeSemesterName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public List<Semester> getSemesters() {
        return semesters;
    }
    // ------------------ Semester Management ------------------

    public void addSemester(Semester semester) {
        if (semester != null) {
            this.semesters.add(semester);
        }
    }

    public void removeSemester(Semester semester) {
        this.semesters.remove(semester);
    }

    // ------------------ GPA & Academic Calculations ------------------
    /**
     * Calculates the cumulative GPA across all semesters (on a 4.0 scale).
     * Excludes dropped courses and active courses with no graded assessments.
     * The GPA is credit-weighted.
     *
     * @return Cumulative GPA, or 0.0 if no applicable courses exist.
     */
    public double getCumulativeGPA() {
        double totalPoints = 0;
        int totalCredits = 0;

        for (Semester s : semesters) {
            for (Course c : s.getCourses()) {
                // Skip dropped courses
                if (c.getStatus() == CourseStatus.DROPPED) continue;
                // Skip active courses that have no graded work
                if (c.getStatus() == CourseStatus.ACTIVE &&
                    c.getAssessments().stream().noneMatch(Assessment::isGraded)) continue;

                double gpaPoints = convertToGPAPoints(c.getCurrentGrade());
                totalPoints += gpaPoints * c.getCredits();
                totalCredits += c.getCredits();
            }
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    /**
     * Calculates the GPA considering only courses with status COMPLETED.
     * Used for final, confirmed course results.
     *
     * @return Completed-course GPA, or 0.0 if no completed courses exist.
     */
    public double getCompletedGPA() {
        double totalPoints = 0;
        int totalCredits = 0;

        for (Semester s : semesters) {
            for (Course c : s.getCourses()) {
                // Only include courses that have been fully completed
                if (c.getStatus() != CourseStatus.COMPLETED) continue;

                double gpaPoints = convertToGPAPoints(c.getCurrentGrade());
                totalPoints += gpaPoints * c.getCredits();
                totalCredits += c.getCredits();
            }
        }
        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    /**
     * Returns the total number of credit hours for courses marked as COMPLETED.
     *
     * @return Total completed credits.
     */
    public int getTotalCreditsCompleted() {
        int total = 0;
        for (Semester s : semesters) {
            for (Course c : s.getCourses()) {
                if (c.getStatus() == CourseStatus.COMPLETED) {
                    total += c.getCredits();
                }
            }
        }
        return total;
    }

    /**
     * Determines the student's academic standing based on cumulative GPA.
     *
     * @return "New Student" (no semesters), "Good Standing" (GPA ≥ 3.0),
     *         "Satisfactory" (GPA ≥ 2.0), or "Academic Probation" (below 2.0).
     */
    public String getAcademicStanding() {
        if (semesters == null || semesters.isEmpty()) return "New Student";

        double gpa = getCumulativeGPA();
        if (gpa >= 3.0) return "Good Standing";
        if (gpa >= 2.0) return "Satisfactory";
        return "Academic Probation";
    }

    /**
     * Converts a percentage grade (0–100) to a 4.0 GPA point.
     *
     * @param grade The percentage grade.
     * @return The corresponding GPA value.
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