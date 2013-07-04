package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.GateKeeper;

public class InstructorCourseEditPageAction extends Action {
	protected static final Logger log = Config.getLogger();
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException { 
		
		InstructorCourseEditPageData data = new InstructorCourseEditPageData(account);
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
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
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, data);
		
	}


}
