package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * PageData: this is page data for 'Enroll' page for a course of an instructor.
 */
public class InstructorCourseEnrollPageData extends PageData {
    private String courseId;
    private String enrollStudents;
    private boolean isOpenOrPublishedEmailSentForTheCourse;

    public InstructorCourseEnrollPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

    public InstructorCourseEnrollPageData(AccountAttributes account, String sessionToken, String courseId,
            String enrollStudents, boolean isOpenOrPublishedEmailSentForTheCourse) {
        super(account, sessionToken);
        this.courseId = courseId;
        this.enrollStudents = enrollStudents;
        this.isOpenOrPublishedEmailSentForTheCourse = isOpenOrPublishedEmailSentForTheCourse;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEnrollStudents() {
        return enrollStudents;
    }

    public boolean getOpenOrPublishedEmailSentForTheCourse() {
        return isOpenOrPublishedEmailSentForTheCourse;
    }

    public String getInstructorCourseEnrollSaveLink() {
        return getInstructorCourseEnrollSaveLink(courseId);
    }

    public String getInstructorCourseEnrollUpdateLink() {
        return getInstructorCourseEnrollUpdateLink(courseId);
    }
}
