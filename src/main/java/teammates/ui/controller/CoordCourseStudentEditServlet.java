package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Instructor course student edit page.
 */
public class InstructorCourseStudentEditServlet extends
		ActionServlet<InstructorCourseStudentEditHelper> {

	@Override
	protected InstructorCourseStudentEditHelper instantiateHelper() {
		return new InstructorCourseStudentEditHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req,
			InstructorCourseStudentEditHelper helper)
			throws EntityDoesNotExistException {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);

		boolean submit = (req.getParameter("submit") != null);
		String studentName = req.getParameter(Common.PARAM_STUDENT_NAME);
		String newEmail = req.getParameter(Common.PARAM_NEW_STUDENT_EMAIL);
		String teamName = req.getParameter(Common.PARAM_TEAM_NAME);
		String comments = req.getParameter(Common.PARAM_COMMENTS);

		helper.student = helper.server.getStudent(courseID, studentEmail);
		helper.regKey = helper.server.getKeyForStudent(courseID, studentEmail);

		if (submit) {
			helper.student.name = studentName;
			helper.student.email = newEmail;
			helper.student.team = teamName;
			helper.student.comments = comments;
			try {
				helper.server.editStudent(studentEmail, helper.student);
				helper.statusMessage = Common.MESSAGE_STUDENT_EDITED;
				helper.redirectUrl = helper.getInstructorCourseDetailsLink(courseID);
			} catch (InvalidParametersException e) {
				helper.statusMessage = e.getMessage();
				helper.error = true;
				return;
			}
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_STUDENT_EDIT;
	}
}
