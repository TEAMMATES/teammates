package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Instructor course student details page.
 */
public class InstructorCourseStudentDetailsServlet extends ActionServlet<InstructorCourseStudentDetailsHelper> {

	@Override
	protected InstructorCourseStudentDetailsHelper instantiateHelper() {
		return new InstructorCourseStudentDetailsHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, InstructorCourseStudentDetailsHelper helper) throws EntityDoesNotExistException{
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }  
        
        String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		helper.student = helper.server.getStudent(courseID, studentEmail);
		helper.regKey = helper.server.getKeyForStudent(courseID, studentEmail);
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		data.add(studentEmail);
		  
        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_STUDENT_DETAILS_SERVLET, Common.INSTRUCTOR_COURSE_STUDENT_DETAILS_SERVLET_PAGE_LOAD,
        		true, helper, url, data);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_STUDENT_DETAILS;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_STUDENT_DETAILS_SERVLET_PAGE_LOAD){
			try {
				params = "instructorCourseStudentDetails Page Load<br>";
				params += "Viewing details for Student <span class=\"bold\">" + (String)data.get(1) + "</span> in Course <span class=\"bold\">[" + (String)data.get(0) + "</span>"; 
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
