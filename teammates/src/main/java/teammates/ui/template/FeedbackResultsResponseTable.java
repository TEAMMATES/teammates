package teammates.ui.template;

import java.util.List;

public class FeedbackResultsResponseTable {
    private String recipientName;
    private List<FeedbackResultsResponse> responses;

    public FeedbackResultsResponseTable(String recipientName, List<FeedbackResultsResponse> responses) {
        this.recipientName = recipientName;
        this.responses = responses;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public List<FeedbackResultsResponse> getResponses() {
        return responses;
    }

    public boolean isGiverNameYou() {
        return !responses.isEmpty() && "You".equals(responses.get(0).getGiverName());
    }
}
