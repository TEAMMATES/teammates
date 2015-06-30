package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Action: showing page to enroll students into a course for an instructor
 */
public class InstructorCourseEnrollPageAction extends Action {
    
    @Override
    public ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_ENROLLMENT_INFO);

        Assumption.assertNotNull(courseId);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        
        /* Setup page data for 'Enroll' page of a course */
        InstructorCourseEnrollPageData pageData = new InstructorCourseEnrollPageData(account, courseId, studentsInfo);

        statusToAdmin = "instructorCourseEnroll Page Load<br>"
                + "Enrollment for Course <span class=\"bold\">[" + courseId + "]</span>"; 
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageData);
        return response;
    }
}
