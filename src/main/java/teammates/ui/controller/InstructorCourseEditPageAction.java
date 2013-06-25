package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.GateKeeper;

public class InstructorCourseEditPageAction extends Action {
	protected static final Logger log = Common.getLogger();
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException { 
		
		InstructorCourseEditPageData data = new InstructorCourseEditPageData(account);
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getCourse(courseId));
		
		data.course = logic.getCourse(courseId);
		data.instructorList = logic.getInstructorsForCourse(courseId);
		if(data.course == null || data.instructorList == null){
			throw new EntityDoesNotExistException("Course "+courseId+" does not exist");
		} 

		statusToAdmin = "instructorCourseEdit Page Load<br>"
				+ "Editing information for Course <span class=\"bold\">["
				+ courseId + "]</span>";
		
		return createShowPageResult(Common.JSP_INSTRUCTOR_COURSE_EDIT, data);
		
	}


}
