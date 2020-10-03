package teammates.ui.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * The API output format of a list of {@link FeedbackSessionAttributes}.
 */
public class FeedbackSessionsData extends ApiOutput {
    private final List<FeedbackSessionData> feedbackSessions;

    public FeedbackSessionsData(List<FeedbackSessionAttributes> feedbackSessionAttributesList) {
        this.feedbackSessions =
                feedbackSessionAttributesList.stream().map(FeedbackSessionData::new).collect(Collectors.toList());
    }

    public List<FeedbackSessionData> getFeedbackSessions() {
        return feedbackSessions;
    }
}
