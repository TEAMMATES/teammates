package teammates.ui.output;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.storage.entity.FeedbackResponse;

/**
 * The API output format for submitted {@link FeedbackResponse}, grouped by question.
 */
public class FeedbackQuestionResponsesData implements ApiOutput {

    private Map<UUID, List<FeedbackResponseData>> questionResponses;

    private FeedbackQuestionResponsesData(Map<UUID, List<FeedbackResponseData>> questionResponses) {
        this.questionResponses = questionResponses;
    }

    public FeedbackQuestionResponsesData() {
        questionResponses = Collections.emptyMap();
    }

    /**
     * Creates FeedbackQuestionResponsesData from a list of FeedbackResponse.
     */
    public static FeedbackQuestionResponsesData createFromEntity(List<FeedbackResponse> responses) {
        Map<UUID, List<FeedbackResponseData>> groupedResponses = responses.stream()
                .collect(Collectors.groupingBy(
                        FeedbackResponse::getQuestionId,
                        LinkedHashMap::new,
                        Collectors.mapping(FeedbackResponseData::new, Collectors.toList())));
        return new FeedbackQuestionResponsesData(groupedResponses);
    }

    public Map<UUID, List<FeedbackResponseData>> getQuestionResponses() {
        return questionResponses;
    }

    public void setQuestionResponses(Map<UUID, List<FeedbackResponseData>> questionResponses) {
        this.questionResponses = questionResponses;
    }
}
