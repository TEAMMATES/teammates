package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.StudentAttributes;

public class CourseDetailsStudentsTableRow {
    public List<ElementTag> actions;
    public List<ElementTag> commentActions;
    public StudentAttributes student;
    
    public CourseDetailsStudentsTableRow() {
        this.actions = new ArrayList<ElementTag>();
        this.commentActions = new ArrayList<ElementTag>();
    }
    
    public List<ElementTag> getActions() {
        return this.actions;
    }
    
    public List<ElementTag> getCommentActions() {
        return this.commentActions;
    }
    
    public StudentAttributes getStudent() {
        return this.student;
    }
}
