package com.academictracker.prediction2.engine;

import com.academictracker.prediction2.model.TranscriptInput;
import com.academictracker.prediction2.policy.GradeScale;
import com.academictracker.prediction2.policy.ValidationPolicy;
import com.academictracker.prediction2.result.GpaProjectionResult;
import com.academictracker.prediction2.result.PredictionStatus;
import com.academictracker.prediction2.result.TargetGpaResult;

/**
 * Calculates the GPA required on remaining credits to reach a cumulative target.
 */
public class TargetGpaEngine {
    private final GpaProjectionEngine gpaProjectionEngine;

    public TargetGpaEngine() {
        this(new GpaProjectionEngine());
    }

    public TargetGpaEngine(GpaProjectionEngine gpaProjectionEngine) {
        this.gpaProjectionEngine = gpaProjectionEngine;
    }

    public TargetGpaResult calculateRequiredFutureGpa(TranscriptInput transcript, double targetGpa) {
        ValidationPolicy.requireTranscript(transcript);
        ValidationPolicy.requireTargetGpa(targetGpa);
        ValidationPolicy.requireRemainingCredits(transcript.remainingCredits());

        GpaProjectionResult projection = gpaProjectionEngine.project(transcript);
        double currentOfficialGpa = projection.officialGpa();
        int completedCredits = projection.officialCredits();
        int remainingCredits = transcript.remainingCredits();

        if (currentOfficialGpa + 0.0001 >= targetGpa && completedCredits > 0) {
            return new TargetGpaResult(
                    currentOfficialGpa,
                    targetGpa,
                    completedCredits,
                    remainingCredits,
                    0.0,
                    PredictionStatus.ALREADY_ACHIEVED,
                    "Current official GPA already meets or exceeds the target."
            );
        }

        double requiredFutureGpa = (targetGpa * (completedCredits + remainingCredits)
                - currentOfficialGpa * completedCredits) / remainingCredits;

        if (requiredFutureGpa > GradeScale.MAX_GPA + 0.0001) {
            return new TargetGpaResult(
                    currentOfficialGpa,
                    targetGpa,
                    completedCredits,
                    remainingCredits,
                    requiredFutureGpa,
                    PredictionStatus.IMPOSSIBLE,
                    "Required future GPA is above 4.0, so the target is impossible with the remaining credits."
            );
        }

        return new TargetGpaResult(
                currentOfficialGpa,
                targetGpa,
                completedCredits,
                remainingCredits,
                Math.max(0.0, requiredFutureGpa),
                PredictionStatus.ACHIEVABLE,
                "The target is achievable if remaining credits average about "
                        + String.format("%.2f", Math.max(0.0, requiredFutureGpa)) + " GPA."
        );
    }
}
