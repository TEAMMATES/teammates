package teammates.ui.output;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.storage.entity.FeedbackSession;

/**
 * The API output format of a list of {@link FeedbackSession}.
 */
public class FeedbackSessionsData implements ApiOutput {
    private final List<FeedbackSessionViewData> feedbackSessions;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private FeedbackSessionsData(FeedbackSessionViewData[] feedbackSessions) {
        this.feedbackSessions = Arrays.asList(feedbackSessions);
    }

    public FeedbackSessionsData(List<FeedbackSession> feedbackSessionList) {
        this.feedbackSessions = feedbackSessionList.stream()
                .map(session -> new FeedbackSessionViewData(new FeedbackSessionData(session)))
                .collect(Collectors.toList());
    }

    public FeedbackSessionsData(Map<FeedbackSession, Instant> feedbackSessionToDeadline) {
        this.feedbackSessions = feedbackSessionToDeadline.entrySet().stream()
                .map(e -> new FeedbackSessionViewData(new FeedbackSessionData(e.getKey(), e.getValue())))
                .collect(Collectors.toList());
    }

    public List<FeedbackSessionViewData> getFeedbackSessions() {
        return feedbackSessions;
    }

}
