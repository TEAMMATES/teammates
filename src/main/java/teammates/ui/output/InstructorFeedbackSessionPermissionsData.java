package teammates.ui.output;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The instructor permissions exposed for feedback session responses.
 */
public class InstructorFeedbackSessionPermissionsData implements ApiOutput {

    private final boolean canModifySession;
    private final boolean canSubmitSession;
    private final boolean canViewSession;

    @JsonCreator
    public InstructorFeedbackSessionPermissionsData(boolean canModifySession,
            boolean canSubmitSession, boolean canViewSession) {
        this.canModifySession = canModifySession;
        this.canSubmitSession = canSubmitSession;
        this.canViewSession = canViewSession;
    }

    public boolean getCanModifySession() {
        return canModifySession;
    }

    public boolean getCanSubmitSession() {
        return canSubmitSession;
    }

    public boolean getCanViewSession() {
        return canViewSession;
    }
}
