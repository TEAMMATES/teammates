package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Instructor View Course Details action
 *
 */
public class InstructorCourseDetailsServlet extends ActionServlet<InstructorCourseDetailsHelper> {

	@Override
	protected InstructorCourseDetailsHelper instantiateHelper() {
		return new InstructorCourseDetailsHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorCourseDetailsHelper helper) throws EntityDoesNotExistException{
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		
		if(courseID!=null){
			helper.course = helper.server.getCourseDetails(courseID);
			helper.students = helper.server.getStudentListForCourse(courseID);
			helper.instructors = helper.server.getInstructorsByCourseId(courseID);
		} else {
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE;
		}
		
		sortStudents(helper.students);
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        } 
        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_DETAILS_SERVLET, Common.INSTRUCTOR_COURSE_DETAILS_SERVLET_PAGE_LOAD,
        		true, helper, url, data);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_DETAILS;
	}


	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		InstructorCourseDetailsHelper h = (InstructorCourseDetailsHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_DETAILS_SERVLET_PAGE_LOAD){
			try {
				params = "instructorCourseDetails Page Load<br>";
				params += "Viewing Course Details for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}
}
