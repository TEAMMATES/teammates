package teammates.ui.template;

import java.util.List;

public class FeedbackSessionsCopyFromModal {
    
    private List<FeedbackSessionRow> existingFeedbackSessions;
    private String fsName;
    private List<ElementTag> coursesSelectField;

    public FeedbackSessionsCopyFromModal(List<FeedbackSessionRow> existingFeedbackSessions, 
                                         String fsName, 
                                         List<ElementTag> coursesSelectField) {
        this.existingFeedbackSessions = existingFeedbackSessions;
        this.fsName = fsName;
        this.coursesSelectField = coursesSelectField;
    }
    
    
    public List<FeedbackSessionRow> getExistingFeedbackSessions() {
        return existingFeedbackSessions;
    }
    
    public String getFsName() {
        return fsName;
    }

    public List<ElementTag> getCoursesSelectField() {
        return coursesSelectField;
    }
    
}
