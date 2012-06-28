package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.CourseData;
import teammates.jsp.CoordCourseStudentEditHelper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Coordinator course student edit page.
 */
public class CoordCourseStudentEditServlet extends
		ActionServlet<CoordCourseStudentEditHelper> {

	@Override
	protected CoordCourseStudentEditHelper instantiateHelper() {
		return new CoordCourseStudentEditHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseStudentEditHelper helper)
			throws IOException {
		if (!helper.user.isCoord && !helper.user.isAdmin) {
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req,
			CoordCourseStudentEditHelper helper)
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
				helper.redirectUrl = helper.getCoordCourseDetailsLink(courseID);
			} catch (InvalidParametersException e) {
				helper.statusMessage = e.getMessage();
				helper.error = true;
				return;
			}
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_COURSE_STUDENT_EDIT;
	}
}
