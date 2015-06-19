package teammates.ui.template;

import java.util.List;

public class FeedbackSessionsTable {
    
    private String feedbackSessionNameToHighlight;
    private String courseIdForHighlight;
    
    private List<FeedbackSessionsTableRow> existingFeedbackSessions;
        
    public FeedbackSessionsTable(List<FeedbackSessionsTableRow> existingFeedbackSessions, 
                                 String feedbackSessionNameToHighlight, 
                                 String courseIdForHighlight) {
        this.existingFeedbackSessions = existingFeedbackSessions;
        this.feedbackSessionNameToHighlight = feedbackSessionNameToHighlight;
        this.courseIdForHighlight = courseIdForHighlight;
    }
    
    public List<FeedbackSessionsTableRow> getExistingFeedbackSessions() {
        return existingFeedbackSessions;
    }

    public String getFeedbackSessionNameToHighlight() {
        return feedbackSessionNameToHighlight;
    }

    public String getCourseIdForHighlight() {
        return courseIdForHighlight;
    }

}
