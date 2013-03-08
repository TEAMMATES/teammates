package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.UserType;
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
		
		String url = req.getRequestURI();
		if (req.getQueryString() != null){
			url += "?" + req.getQueryString();
		}
		activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DETAILS_SERVLET, Common.ADMIN_ACCOUNT_DETAILS_SERVLET_PAGE_LOAD,
				false, helper, url, null);
	}
	
	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACCOUNT_DETAILS;
	}

	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		AdminAccountDetailsHelper h = (AdminAccountDetailsHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.ADMIN_ACCOUNT_DETAILS_SERVLET_PAGE_LOAD){
			try {
				params = "adminAccountDetails Page Load<br>";
				params += "Viewing details for " + h.accountInformation.name + "(" +h.accountInformation.googleId + ")";
			} catch (NullPointerException e) {
				params = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else if (action == Common.LOG_SERVLET_ACTION_FAILURE) {
            String e = (String)data.get(0);
            params = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
            params += e + "</span>";
        } else {
			params = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
			
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}
	
	
}
