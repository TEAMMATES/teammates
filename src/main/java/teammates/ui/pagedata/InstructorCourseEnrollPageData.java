package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * PageData: this is page data for 'Enroll' page for a course of an instructor.
 */
public class InstructorCourseEnrollPageData extends PageData {
    private String courseId;
    private String enrollStudents;

    public InstructorCourseEnrollPageData(AccountAttributes account, String sessionToken, String courseId,
            String enrollStudents) {
        super(account, sessionToken);
        this.courseId = courseId;
        this.enrollStudents = enrollStudents;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getEnrollStudents() {
        return enrollStudents;
    }

    public String getInstructorCourseEnrollSaveLink() {
        return getInstructorCourseEnrollSaveLink(courseId);
    }
}
