package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;

@SuppressWarnings("serial")
/**
 * Servlet to handle Delete Course action
 * @author Aldrian Obaja
 *
 */
public class InstructorCourseStudentDeleteServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) {
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        } 
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		helper.server.deleteStudent(courseID, studentEmail);
		helper.statusMessage = Common.MESSAGE_STUDENT_DELETED;
		helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		helper.redirectUrl = Common.addParamToUrl(helper.redirectUrl,Common.PARAM_COURSE_ID,courseID);
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		data.add(studentEmail);
				   
        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET, Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET_DELETE_STUDENT,
        		true, helper, url, data);
	}


	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET_DELETE_STUDENT){
			try {
				params = "Student <span class=\"bold\">" + (String)data.get(1) + "</span> in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> deleted.";
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
