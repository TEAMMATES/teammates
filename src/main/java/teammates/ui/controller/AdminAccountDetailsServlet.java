package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.CourseDataDetails;
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
			helper.instructorCourseList = new ArrayList<CourseDataDetails>(helper.server.getCourseListForInstructor(googleId).values());
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
		
		String url = getRequestedURL(req);
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(helper.accountInformation.googleId);
		data.add(helper.accountInformation.name);
		activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DETAILS_SERVLET, Common.ADMIN_ACCOUNT_DETAILS_SERVLET_PAGE_LOAD,
				false, helper, url, data);
	}
	
	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACCOUNT_DETAILS;
	}

	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.ADMIN_ACCOUNT_DETAILS_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
			
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "adminAccountDetails Page Load<br>";
			message += "Viewing details for " + (String)data.get(1) + "(" + (String)data.get(0) + ")";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
