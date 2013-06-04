package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.logic.GateKeeper;

public class InstructorCourseEnrollPageAction extends Action {
	protected static final Logger log = Common.getLogger();
	
	
	@Override
	public ActionResult execute() {
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		new GateKeeper().verifyCourseInstructorOrAbove(courseId);
		
		InstructorCourseEnrollPageData data = new InstructorCourseEnrollPageData(account);
		data.courseId = courseId;
		
		statusToAdmin = "instructorCourseEnroll Page Load<br>"
				+ "Enrollment for Course <span class=\"bold\">[" + courseId + "]</span>"; 
		
		return createShowPageResult(Common.JSP_INSTRUCTOR_COURSE_ENROLL, data);
	}


}
