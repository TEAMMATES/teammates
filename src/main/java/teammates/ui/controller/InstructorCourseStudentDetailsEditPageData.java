package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class InstructorCourseStudentDetailsEditPageData extends InstructorCourseStudentDetailsPageData {
    
    private String newEmail;
    private boolean isAnyEmailSentForTheCourse;

    public InstructorCourseStudentDetailsEditPageData(
            AccountAttributes account, StudentAttributes student, boolean hasSection,
            boolean isAnyEmailSentForTheCourse) {
        this(account, student, student.email, hasSection, isAnyEmailSentForTheCourse);
    }
    
    public InstructorCourseStudentDetailsEditPageData(
            AccountAttributes account, StudentAttributes student, String newEmail, boolean hasSection,
            boolean isAnyEmailSentForTheCourse) {
        super(account, student, null, false, hasSection, null);
        this.newEmail = newEmail;
        this.isAnyEmailSentForTheCourse = isAnyEmailSentForTheCourse;
    }

    public boolean isAnyEmailSentForTheCourse() {
        return isAnyEmailSentForTheCourse;
    }

    public String getNewEmail() {
        return newEmail;
    }
}
