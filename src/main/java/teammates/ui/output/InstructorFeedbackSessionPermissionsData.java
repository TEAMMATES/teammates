package teammates.ui.output;

/**
 * The instructor permissions exposed for feedback session responses.
 */
public class InstructorFeedbackSessionPermissionsData implements ApiOutput {

    private final boolean canModifySession;
    private final boolean canSubmitSessionInSections;
    private final boolean canViewSessionInSections;

    public InstructorFeedbackSessionPermissionsData(boolean canModifySession,
            boolean canSubmitSessionInSections, boolean canViewSessionInSections) {
        this.canModifySession = canModifySession;
        this.canSubmitSessionInSections = canSubmitSessionInSections;
        this.canViewSessionInSections = canViewSessionInSections;
    }

    public boolean getCanModifySession() {
        return canModifySession;
    }

    public boolean getCanSubmitSessionInSections() {
        return canSubmitSessionInSections;
    }

    public boolean getCanViewSessionInSections() {
        return canViewSessionInSections;
    }
}
