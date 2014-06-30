package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class InstructorCourseStudentDetailsPageData extends PageData {
    
    public InstructorAttributes currentInstructor;
    public StudentAttributes student;
    public String regKey;
    public boolean hasSection;
    public String commentRecipient;

    public InstructorCourseStudentDetailsPageData(AccountAttributes account) {
        super(account);
    }

}
