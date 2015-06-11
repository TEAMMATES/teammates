package teammates.ui.template;

import java.util.List;

public class FeedbackSessionsList {
    
    private List<FeedbackSessionRow> existingFeedbackSessions;
    
    public FeedbackSessionsList(List<FeedbackSessionRow> existingFeedbackSessions) {
        this.existingFeedbackSessions = existingFeedbackSessions;
    }
    
    public List<FeedbackSessionRow> getExistingFeedbackSessions() {
        return existingFeedbackSessions;
    }
    
}
