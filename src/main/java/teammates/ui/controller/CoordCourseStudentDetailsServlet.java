package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Coordinator course student details page.
 */
public class CoordCourseStudentDetailsServlet extends ActionServlet<CoordCourseStudentDetailsHelper> {

	@Override
	protected CoordCourseStudentDetailsHelper instantiateHelper() {
		return new CoordCourseStudentDetailsHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordCourseStudentDetailsHelper helper) throws EntityDoesNotExistException{
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		helper.student = helper.server.getStudent(courseID, studentEmail);
		helper.regKey = helper.server.getKeyForStudent(courseID, studentEmail);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_COURSE_STUDENT_DETAILS;
	}
}
