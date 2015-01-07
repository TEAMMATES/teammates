package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;

public class InstructorFeedbackEditCopyData extends PageData {
    public List<CourseAttributes> courses;
    
    public InstructorFeedbackEditCopyData(AccountAttributes account) {
        super(account);
    }
}
