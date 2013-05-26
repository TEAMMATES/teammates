package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle the 'add course' action for instructors.
 */
public class InstructorCourseAddServlet extends ActionServlet<InstructorCourseHelper> {

	@Override
	protected InstructorCourseHelper instantiateHelper() {
		return new InstructorCourseHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorCourseHelper helper)
			throws EntityDoesNotExistException {

		helper.createCourse(req);
		helper.loadCourseList();
		helper.setStatus();
		generateLogEntry(req, helper);
		
	}

	private void generateLogEntry(HttpServletRequest req,
			InstructorCourseHelper helper) {
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(helper.courseID);
		data.add(helper.courseName);
		data.add(helper.instructorList);
		activityLogEntry = instantiateActivityLogEntry(
				Common.INSTRUCTOR_COURSE_SERVLET,
				Common.INSTRUCTOR_COURSE_SERVLET_ADD_COURSE,
				true, helper, getRequestedURL(req), data);
	}


	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE;
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		return InstructorCourseHelper
				.generateActivityLogEntryMessageForCourseAdd(servletName, action, data);

	}
	
}
