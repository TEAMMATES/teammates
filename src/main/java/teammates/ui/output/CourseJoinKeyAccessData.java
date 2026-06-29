package teammates.ui.output;

import teammates.common.datatransfer.CourseJoinKeyAccessDecision;

/**
 * Result returned by the course join key access preflight endpoint.
 */
public class CourseJoinKeyAccessData implements ApiOutput {
    private final CourseJoinKeyAccessDecision decision;
    private final String message;

    public CourseJoinKeyAccessData(CourseJoinKeyAccessDecision decision, String message) {
        this.decision = decision;
        this.message = message;
    }

    public CourseJoinKeyAccessDecision getDecision() {
        return decision;
    }

    public String getMessage() {
        return message;
    }
}
