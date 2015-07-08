package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

/**
 * PageData: this is page data for 'Enroll' page for a course of an instructor
 */
public class InstructorCourseEnrollPageData extends PageData {
    private String courseId;
    private String enrollStudents;
    
    public InstructorCourseEnrollPageData(AccountAttributes account, String courseId, String enrollStudents) {
        super(account);
        this.courseId =  sanitizeForHtml(courseId);
        this.enrollStudents = sanitizeForHtml(enrollStudents);
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
