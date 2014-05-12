package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

/**
 * PageData: this is page data for 'Enroll' page for a course of an instructor
 */
public class InstructorCourseEnrollPageData extends PageData {
    
    public InstructorCourseEnrollPageData(AccountAttributes account) {
        super(account);
        enrollStudents = "";
    }

    public String courseId;
    
    //Note: Must not be null as it will be accessed by instructorCourseEnroll.jsp
    public String enrollStudents;

}
