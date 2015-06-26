package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;


public class InstructorCourseStudentDetailsEditPageData extends  InstructorCourseStudentDetailsPageData{
    
    private String newEmail;

    public InstructorCourseStudentDetailsEditPageData(
            AccountAttributes account, StudentAttributes student, boolean hasSection) {
        this(account, student, student.email, hasSection);
    }
    
    public InstructorCourseStudentDetailsEditPageData(
            AccountAttributes account, StudentAttributes student, String newEmail, boolean hasSection) {
        super(account, student, null, false, hasSection, null);
        this.newEmail = newEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }
}
