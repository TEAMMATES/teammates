package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
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
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		helper.student = helper.server.getStudent(courseID, studentEmail);
		helper.regKey = helper.server.getKeyForStudent(courseID, studentEmail);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_STUDENT_DETAILS;
	}
}
