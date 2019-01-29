package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * The output format for a list of feedback session.
 */
public class FeedbackSessions extends ApiOutput {
    private final List<FeedbackSession> feedbackSessions;

    public FeedbackSessions(List<FeedbackSessionAttributes> feedbackSessionAttributesList) {
        this.feedbackSessions =
                feedbackSessionAttributesList.stream().map(FeedbackSession::new).collect(Collectors.toList());
    }

    public List<FeedbackSession> getFeedbackSessions() {
        return feedbackSessions;
    }
}
