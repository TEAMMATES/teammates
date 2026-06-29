package teammates.ui.output;

import teammates.common.datatransfer.SessionKeyAccessDecision;

/**
 * Result returned by the session-key access preflight endpoint.
 */
public class SessionKeyAccessData implements ApiOutput {
    private final SessionKeyAccessDecision decision;
    private final String message;

    public SessionKeyAccessData(SessionKeyAccessDecision decision, String message) {
        this.decision = decision;
        this.message = message;
    }

    public SessionKeyAccessDecision getDecision() {
        return decision;
    }

    public String getMessage() {
        return message;
    }
}
