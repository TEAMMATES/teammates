package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.StudentAttributes;

public class CourseDetailsStudentsTableRow {
    private List<ElementTag> actions;
    private List<ElementTag> commentActions;
    private List<ElementTag> commentRecipientOptions;
    private StudentAttributes student;
    
    public CourseDetailsStudentsTableRow(StudentAttributes student) {
        this.student = student;
        actions = new ArrayList<ElementTag>();
        commentActions = new ArrayList<ElementTag>();
        commentRecipientOptions = new ArrayList<ElementTag>();
    }
    
    public List<ElementTag> getActions() {
        return actions;
    }
    
    public List<ElementTag> getCommentActions() {
        return commentActions;
    }
    
    public StudentAttributes getStudent() {
        return student;
    }
    
    public List<ElementTag> getCommentRecipientOptions() {
        return commentRecipientOptions;
    }

    public void setActions(List<ElementTag> actionButtons) {
        actions = actionButtons;        
    }

    public void setCommentActionButtons(List<ElementTag> createCommentActionButtons) {
        commentActions = createCommentActionButtons;
        
    }

    public void setCommentRecipientOptions(List<ElementTag> createCommentRecipientOptions) {
        commentRecipientOptions = createCommentRecipientOptions;
    }
}
