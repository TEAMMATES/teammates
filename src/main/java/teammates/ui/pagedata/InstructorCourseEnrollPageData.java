package teammates.ui.pagedata;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * PageData: this is page data for 'Enroll' page for a course of an instructor.
 */
public class InstructorCourseEnrollPageData extends PageData {
    private String courseId;
    private String enrollStudents;
    private Map<String, String> enrollErrorLines = new HashMap<>();

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

    public Map<String, String> getEnrollErrorLines() {
        return enrollErrorLines;
    }

    public void setEnrollErrorLines(Map<String, String> enrollErrorLines) {
        this.enrollErrorLines = enrollErrorLines;
    }
}
