package com.tracker.academictracker.domain.model;

/**
 * Represents an assessment (exam, assignment, project, etc.) within the academic tracker.
 * Each assessment has a name, category, weight, achieved score, and maximum score.
 */
public class Assessment {
    private String assessmentName; 
    private String category;       
    private double weight; 
    private Double score;       
    private double maxScore;     

    public Assessment() {
        // For Gson
    }

    public Assessment(String assessmentName, String category, double weight, double maxScore) {
        this.assessmentName = assessmentName;
        this.category = category;
        setWeight(weight); // Use setter to enforce validation
        this.maxScore = maxScore;
        this.score = null; // Initially ungraded
    }

    // ==================== GETTERS & SETTERS ====================

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getMaxScore() {
        return maxScore;
    }

    /**
     * Sets the maximum possible score. Must be greater than 0.
     *
     * @param maxScore The maximum score.
     * @throws IllegalArgumentException if maxScore <= 0.
     */
    public void setMaxScore(double maxScore) {
        if (maxScore <= 0) {
            throw new IllegalArgumentException("Max score must be greater than 0");
        }
        this.maxScore = maxScore;
    }

    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the assessment (0–100%).
     *
     * @param weight The weight percentage.
     * @throws IllegalArgumentException if weight is outside [0, 100].
     */
    public void setWeight(double weight) {
        if (weight < 0 || weight > 100) {
            throw new IllegalArgumentException("Weight must be between 0 and 100%");
        }
        this.weight = weight;
    }

    /**
     * Returns the achieved score, or null if the assessment hasn't been graded yet.
     */
    public Double getScore() {
        return score;
    }

    /**
     * Sets the achieved score. The score must be between 0 and maxScore (inclusive).
     * A null value can be assigned to mark the assessment as ungraded.
     *
     * @param score The points achieved, or null.
     * @throws IllegalArgumentException if score is non-null and outside [0, maxScore].
     */
    public void setScore(Double score) {
        if (score != null && (score < 0 || score > maxScore)) {
            throw new IllegalArgumentException("Score must be between 0 and maxScore");
        }
        this.score = score;
    }

    // ==================== HELPER METHODS ====================
    public boolean isGraded() {
        return score != null;
    }

    /**
     * Calculates the weighted score contributed by this assessment.
     * If the assessment hasn't been graded yet, returns 0.0.
     *
     * Formula: (score / maxScore) * weight
     *
     * @return The weighted score (e.g., 8/10 with weight 20% → 16 weighted points).
     */
    public double getWeightScore() {
        return isGraded() ? (score / maxScore) * weight : 0.0;
    }
}