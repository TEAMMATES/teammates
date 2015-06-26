package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

public class CourseEditFeedbackSessionRow {
    private String feedbackSessionName;
    private List<ElementTag> permissionCheckBoxes;
    
    public CourseEditFeedbackSessionRow(String feedbackSessionName) {
        this.feedbackSessionName = feedbackSessionName;
        permissionCheckBoxes = new ArrayList<ElementTag>();
    }
    
    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }
    
    public List<ElementTag> getPermissionCheckBoxes() {
        return permissionCheckBoxes;
    }
}
