package teammates.ui.template;

import java.util.List;

public class CourseEditFeedbackSessionRow {
    private String feedbackSessionName;
    private List<ElementTag> permissionCheckBoxes;

    public CourseEditFeedbackSessionRow(String feedbackSessionName, List<ElementTag> checkBoxList) {
        this.feedbackSessionName = feedbackSessionName;
        permissionCheckBoxes = checkBoxList;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public List<ElementTag> getPermissionCheckBoxes() {
        return permissionCheckBoxes;
    }
}
