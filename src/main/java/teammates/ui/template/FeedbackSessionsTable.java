package teammates.ui.template;

import java.util.List;

public class FeedbackSessionsTable {
    
    private List<FeedbackSessionRow> existingFeedbackSessions;
    
    public FeedbackSessionsTable(List<FeedbackSessionRow> existingFeedbackSessions) {
        this.existingFeedbackSessions = existingFeedbackSessions;
    }
    
    public List<FeedbackSessionRow> getExistingFeedbackSessions() {
        return existingFeedbackSessions;
    }
    
}
