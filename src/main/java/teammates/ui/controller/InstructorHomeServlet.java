package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Home actions
 */
public class InstructorHomeServlet extends ActionServlet<InstructorHomeHelper> {

	@Override
	protected InstructorHomeHelper instantiateHelper() {
		return new InstructorHomeHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorHomeHelper helper) throws EntityDoesNotExistException{
		HashMap<String, CourseData> courses = helper.server.getCourseDetailsListForInstructor(helper.userId);
		helper.courses = new ArrayList<CourseData>(courses.values());
		sortCourses(helper.courses);
		for(CourseData course: helper.courses){
			sortEvaluationsByDeadline(course.evaluations);
		}
		
		activityLog = instantiateActivityLogEntry(Common.INSTRUCTOR_HOME_SERVLET, Common.INSTRUCTOR_HOME_SERVLET_PAGE_LOAD,
				true, helper);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_HOME;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper) {
		InstructorHomeHelper h = (InstructorHomeHelper) helper;
		String params;
		
		h.user = helper.server.getLoggedInUser();
		h.account = helper.server.getAccount(h.user.id);
		
		try{
			params = "instructorHome Page Load<br>";
			for(CourseData course: h.courses){
				params += " - [" + course.id + "] " + course.name + "<br>";
			}
		} catch (NullPointerException e) {
			params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		return new ActivityLogEntry(servletName, action, true, h.account, params);
	}
}
