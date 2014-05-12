package teammates.ui.controller;

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
        Assumption.assertNotNull(courseId);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
        
        /* Setup page data for 'Enroll' page of a course */
        InstructorCourseEnrollPageData pageData = new InstructorCourseEnrollPageData(account);
        pageData.courseId = courseId;
        
        statusToAdmin = "instructorCourseEnroll Page Load<br>"
                + "Enrollment for Course <span class=\"bold\">[" + courseId + "]</span>"; 
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, pageData);
        return response;
    }
}
