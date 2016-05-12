package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;


public class InstructorCourseStudentDetailsEditPageData extends  InstructorCourseStudentDetailsPageData {
    
    private String newEmail;

    public InstructorCourseStudentDetailsEditPageData(
            final AccountAttributes account, final StudentAttributes student, final boolean hasSection) {
        this(account, student, student.email, hasSection);
    }
    
    public InstructorCourseStudentDetailsEditPageData(
            final AccountAttributes account, final StudentAttributes student, final String newEmail, final boolean hasSection) {
        super(account, student, null, false, hasSection, null);
        this.newEmail = newEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }
}
