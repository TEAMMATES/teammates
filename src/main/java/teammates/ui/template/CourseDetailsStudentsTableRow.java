package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.StudentAttributes;

public class CourseDetailsStudentsTableRow {
    private List<ElementTag> actions;
    private List<ElementTag> commentActions;
    private List<ElementTag> commentRecipientOptions;
    private StudentAttributes student;
    
    public CourseDetailsStudentsTableRow() {
        this.actions = new ArrayList<ElementTag>();
        this.commentActions = new ArrayList<ElementTag>();
        this.commentRecipientOptions = new ArrayList<ElementTag>();
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
    
    public List<ElementTag> getCommentRecipientOptions() {
        return this.commentRecipientOptions;
    }

    public void setStudent(StudentAttributes student) {
        this.student = student;
    }
}
