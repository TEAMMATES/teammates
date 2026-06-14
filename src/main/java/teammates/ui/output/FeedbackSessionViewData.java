package teammates.ui.output;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The API output format of a feedback session view.
 */
public class FeedbackSessionViewData implements ApiOutput {

    private final FeedbackSessionData feedbackSession;
    @Nullable
    private InstructorFeedbackSessionPermissionsData instructorPermissions;
    @Nullable
    private Long userDeadlineExtension;

    public FeedbackSessionViewData(FeedbackSessionData feedbackSession) {
        this.feedbackSession = feedbackSession;
    }

    @JsonCreator
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

    public Long getUserDeadlineExtension() {
        return userDeadlineExtension;
    }

    public void setUserDeadlineExtension(Long userDeadlineExtension) {
        this.userDeadlineExtension = userDeadlineExtension;
    }
}
