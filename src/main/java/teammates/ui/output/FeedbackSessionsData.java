package teammates.ui.output;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.logic.entity.FeedbackSession;

/**
 * The API output format of a list of {@link FeedbackSession}.
 */
public class FeedbackSessionsData extends ApiOutput {
    private final List<FeedbackSessionData> feedbackSessions;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private FeedbackSessionsData(FeedbackSessionData[] feedbackSessions) {
        this.feedbackSessions = Arrays.asList(feedbackSessions);
    }

    public FeedbackSessionsData(List<FeedbackSession> feedbackSessionList) {
        this.feedbackSessions =
                feedbackSessionList.stream().map(FeedbackSessionData::new).collect(Collectors.toList());
    }

    public FeedbackSessionsData(Map<FeedbackSession, Instant> feedbackSessionToDeadline) {
        this.feedbackSessions = feedbackSessionToDeadline.entrySet().stream()
                .map(e -> new FeedbackSessionData(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<FeedbackSessionData> getFeedbackSessions() {
        return feedbackSessions;
    }

}
