package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;

public class InstructorFeedbackEditCopyPageData extends PageData {
    public List<CourseAttributes> courses;
    
    public InstructorFeedbackEditCopyPageData(AccountAttributes account) {
        super(account);
    }
}
