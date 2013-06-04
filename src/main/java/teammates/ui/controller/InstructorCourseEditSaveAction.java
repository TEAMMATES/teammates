package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorCourseEditSaveAction extends Action {
	protected static final Logger log = Common.getLogger();
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		String instructorList = getRequestParam(Common.PARAM_COURSE_INSTRUCTOR_LIST);
		Assumption.assertNotNull(instructorList);
		
		new GateKeeper().verifyInstructorUsingOwnIdOrAbove(account.googleId);
		
		String institute = account.institute;
		try{
			logic.updateCourseInstructors(courseId, instructorList, institute);				
			statusToUser.add(Common.MESSAGE_COURSE_EDITED);
			
			statusToAdmin = "Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>" +
					"New Instructor List: <br> - " + instructorList.replace("\n", "<br> - ");
			
		} catch (InvalidParametersException e){
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			isError = true;
		}
		
		return createRedirectResult(Common.PAGE_INSTRUCTOR_COURSE);

	}

}
