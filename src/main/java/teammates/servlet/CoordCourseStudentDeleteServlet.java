package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.datatransfer.CourseData;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Delete Course action
 * @author Aldrian Obaja
 *
 */
public class CoordCourseStudentDeleteServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, Helper helper) throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) {
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		CourseData course = helper.server.getCourse(courseID);
		if(course!=null && !course.coord.equals(helper.userId)){
			helper.statusMessage = "You are not authorized to delete the student " +
					Helper.escapeForHTML(studentEmail)+" in course "+courseID;
			helper.redirectUrl = Common.PAGE_COORD_COURSE;
			return;
		}
		
		// Process action
		helper.server.deleteStudent(courseID, studentEmail);
		helper.statusMessage = Common.MESSAGE_STUDENT_DELETED;
		helper.redirectUrl = Common.PAGE_COORD_COURSE_DETAILS;
		helper.redirectUrl = Helper.addParam(helper.redirectUrl,Common.PARAM_COURSE_ID,courseID);
	}

	@Override
	protected String getDefaultForwardUrl() {
		// Not used
		return "";
	}
}
