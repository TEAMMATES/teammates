package teammates.ui.template;

import java.util.List;

public class FeedbackSessionsTable {
    
    private String feedbackSessionNameToHighlight;
    private String courseIdToHighlight;
    
    private List<FeedbackSessionsTableRow> existingFeedbackSessions;
        
    public FeedbackSessionsTable(final List<FeedbackSessionsTableRow> existingFeedbackSessions, 
                                 final String feedbackSessionNameToHighlight, 
                                 final String courseIdToHighlight) {
        this.existingFeedbackSessions = existingFeedbackSessions;
        this.feedbackSessionNameToHighlight = feedbackSessionNameToHighlight;
        this.courseIdToHighlight = courseIdToHighlight;
    }
    
    public List<FeedbackSessionsTableRow> getExistingFeedbackSessions() {
        return existingFeedbackSessions;
    }

    public String getFeedbackSessionNameToHighlight() {
        return feedbackSessionNameToHighlight;
    }

    public String getCourseIdForHighlight() {
        return courseIdToHighlight;
    }

}
