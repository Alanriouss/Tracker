package com.academictracker.prediction2.exception;

/**
 * Thrown when report-ready prediction input is structurally invalid.
 *
 * <p>This simplified module uses exceptions for invalid input and result objects
 * for valid-but-unfavorable prediction outcomes such as IMPOSSIBLE.</p>
 */
public class PredictionInputException extends IllegalArgumentException {
    public PredictionInputException(String message) {
        super(message);
    }
}
