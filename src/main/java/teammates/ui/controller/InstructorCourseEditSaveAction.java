package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.GateKeeper;

public class InstructorCourseEditSaveAction extends Action {
	protected static final Logger log = Utils.getLogger();
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		String instructorList = getRequestParam(Const.ParamsNames.COURSE_INSTRUCTOR_LIST);
		Assumption.assertNotNull(instructorList);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		String institute = account.institute;
		try{
			logic.updateCourseInstructors(courseId, instructorList, institute);				
			statusToUser.add(Const.StatusMessages.COURSE_EDITED);
			
			statusToAdmin = "Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>" +
					"New Instructor List: <br> - " + instructorList.replace("\n", "<br> - ");
			
		} catch (InvalidParametersException e){
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			isError = true;
		}
		
		return createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);

	}

}
