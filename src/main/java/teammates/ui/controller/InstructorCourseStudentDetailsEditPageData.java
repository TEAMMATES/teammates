package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class InstructorCourseStudentDetailsEditPageData extends InstructorCourseStudentDetailsPageData {
    
    private String newEmail;
    private boolean isOpenOrPublishedEmailSentForTheCourse;

    public InstructorCourseStudentDetailsEditPageData(
            AccountAttributes account, StudentAttributes student, boolean hasSection,
            boolean isOpenOrPublishedEmailSentForTheCourse) {
        this(account, student, student.email, hasSection, isOpenOrPublishedEmailSentForTheCourse);
    }
    
    public InstructorCourseStudentDetailsEditPageData(
            AccountAttributes account, StudentAttributes student, String newEmail, boolean hasSection,
            boolean isOpenOrPublishedEmailSentForTheCourse) {
        super(account, student, null, false, hasSection, null);
        this.newEmail = newEmail;
        this.isOpenOrPublishedEmailSentForTheCourse = isOpenOrPublishedEmailSentForTheCourse;
    }

    public boolean isOpenOrPublishedEmailSentForTheCourse() {
        return isOpenOrPublishedEmailSentForTheCourse;
    }

    public String getNewEmail() {
        return newEmail;
    }
}
