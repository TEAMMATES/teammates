package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.storage.sqlentity.FeedbackSession;

/**
 * The API output format of a list of {@link FeedbackSessionAttributes}.
 */
public class FeedbackSessionsData extends ApiOutput {
    private final List<FeedbackSessionData> feedbackSessions;

    public FeedbackSessionsData(List<FeedbackSession> feedbackSessionList) {
        this.feedbackSessions =
                feedbackSessionList.stream().map(FeedbackSessionData::new).collect(Collectors.toList());
    }

    /**
     * Hide information for given student email.
     */
    public void hideInformationForStudent(String email) {
        for (FeedbackSessionData fs : feedbackSessions) {
            fs.hideInformationForStudent(email);
        }
    }

    public List<FeedbackSessionData> getFeedbackSessions() {
        return feedbackSessions;
    }

}
