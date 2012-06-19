package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.jsp.CoordCourseStudentDetailsHelper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Coordinator course student details page.
 * @author Aldrian Obaja
 *
 */
public class CoordCourseStudentDetailsServlet extends ActionServlet<CoordCourseStudentDetailsHelper> {

	@Override
	protected CoordCourseStudentDetailsHelper instantiateHelper() {
		return new CoordCourseStudentDetailsHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseStudentDetailsHelper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordCourseStudentDetailsHelper helper) throws EntityDoesNotExistException{
		// Get parameters
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
