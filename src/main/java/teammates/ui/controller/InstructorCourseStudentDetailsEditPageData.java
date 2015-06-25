package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;


public class InstructorCourseStudentDetailsEditPageData extends  InstructorCourseStudentDetailsPageData{
    
    private String newEmail;

    public InstructorCourseStudentDetailsEditPageData(AccountAttributes account) {
        super(account);
    }

    public void init(StudentAttributes student, boolean hasSection) {
        init(student, student.email, hasSection);
    }
    
    public void init(StudentAttributes student, String newEmail, boolean hasSection) {
        this.newEmail = newEmail;
        init(student, null, false, hasSection, null);
    }

    public String getNewEmail() {
        return newEmail;
    }
}
