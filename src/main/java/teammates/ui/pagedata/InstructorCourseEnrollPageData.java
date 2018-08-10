package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * PageData: this is page data for 'Enroll' page for a course of an instructor.
 */
public class InstructorCourseEnrollPageData extends PageData {
    private String courseId;
    private String enrollStudents;
    private boolean isOpenOrPublishedEmailSentForTheCourse;
    private HashMap<String, String> successfulUpdatedLines = new HashMap<>();
    private HashMap<String, String> errorUpdatedLines = new HashMap<>();
    private ArrayList<String> deletedStudents = new ArrayList<>();

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

    public HashMap<String, String> getSuccessfulUpdatedLines() { return successfulUpdatedLines; }

    public void setSuccessfulUpdatedLines(HashMap<String, String> successfulUpdatedLines) {
        this.successfulUpdatedLines = successfulUpdatedLines;
    }

    public HashMap<String, String> getErrorUpdatedLines() { return errorUpdatedLines; }

    public void setErrorUpdatedLines(HashMap<String, String> errorUpdatedLines) {
        this.errorUpdatedLines = errorUpdatedLines;
    }

    public ArrayList<String> getDeletedStudents() { return deletedStudents; }

    public void setDeletedStudents(ArrayList<String> deletedStudents) {
        this.deletedStudents = deletedStudents;
    }
}
