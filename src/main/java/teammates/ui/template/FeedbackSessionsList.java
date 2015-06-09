package teammates.ui.template;

import java.util.List;

public class FeedbackSessionsList {
    
    public List<FeedbackSessionRow> existingFeedbackSessions;
    public List<ElementTag> rowAttributes;
    
    
    public static final int MAX_CLOSED_SESSION_STATS = 5;

    
    public FeedbackSessionsList(List<FeedbackSessionRow> existingFeedbackSessions) {
        this.existingFeedbackSessions = existingFeedbackSessions;
    }
    
    
    public List<FeedbackSessionRow> getExistingFeedbackSessions() {
        return existingFeedbackSessions;
    }
    
    public List<ElementTag> getRowAttributes() {
        return rowAttributes;
    }


    public static int getMaxClosedSessionStats() {
        return MAX_CLOSED_SESSION_STATS;
    }

}
