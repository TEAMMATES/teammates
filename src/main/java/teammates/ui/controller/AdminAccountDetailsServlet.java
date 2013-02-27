package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
public class AdminAccountDetailsServlet extends ActionServlet<AdminAccountDetailsHelper>{

	@Override
	protected AdminAccountDetailsHelper instantiateHelper() {
		return new AdminAccountDetailsHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req,
			AdminAccountDetailsHelper helper)
			throws EntityDoesNotExistException, InvalidParametersException {
		
		String googleId = req.getParameter(Common.PARAM_INSTRUCTOR_ID);
		
		helper.accountInformation = helper.server.getAccount(googleId);
		try{
			helper.instructorCourseList = new ArrayList<CourseData>(helper.server.getCourseListForInstructor(googleId).values());
		} catch (EntityDoesNotExistException e){
			//Not an instructor of any course
			helper.instructorCourseList = null;
		}
		try{
			helper.studentCourseList = helper.server.getCourseListForStudent(googleId);
		} catch(EntityDoesNotExistException e){
			//Not a student of any course
			helper.studentCourseList = null;
		}
	}
	
	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACCOUNT_DETAILS;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName,
			String action, boolean toShow, Helper helper) {
		// TODO Auto-generated method stub
		return null;
	}
}
