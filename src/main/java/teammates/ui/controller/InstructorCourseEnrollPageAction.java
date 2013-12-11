package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorCourseEnrollPageAction extends Action {
	protected static final Logger log = Utils.getLogger();
	
	
	@Override
	public ActionResult execute() {
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		InstructorCourseEnrollPageData data = new InstructorCourseEnrollPageData(account);
		data.courseId = courseId;
		
		statusToAdmin = "instructorCourseEnroll Page Load<br>"
				+ "Enrollment for Course <span class=\"bold\">[" + courseId + "]</span>"; 
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_ENROLL, data);
	}


}
