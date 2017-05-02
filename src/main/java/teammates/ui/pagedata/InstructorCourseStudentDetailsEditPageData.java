package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

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
