package teammates.ui.output;

import jakarta.annotation.Nullable;

/**
 * The API output format of a feedback session view.
 */
public class FeedbackSessionViewData implements ApiOutput {

    private final FeedbackSessionData feedbackSession;
    @Nullable
    private InstructorFeedbackSessionPermissionsData instructorPermissions;

    public FeedbackSessionViewData(FeedbackSessionData feedbackSession) {
        this.feedbackSession = feedbackSession;
    }

    public FeedbackSessionViewData(FeedbackSessionData feedbackSession,
            @Nullable InstructorFeedbackSessionPermissionsData instructorPermissions) {
        this.feedbackSession = feedbackSession;
        this.instructorPermissions = instructorPermissions;
    }

    public FeedbackSessionData getFeedbackSession() {
        return feedbackSession;
    }

    public InstructorFeedbackSessionPermissionsData getInstructorPermissions() {
        return instructorPermissions;
    }

    public void setInstructorPermissions(InstructorFeedbackSessionPermissionsData instructorPermissions) {
        this.instructorPermissions = instructorPermissions;
    }
}
