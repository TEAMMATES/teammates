package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.jsp.CoordCourseDetailsHelper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Coordinator View Course Details action
 * @author Aldrian Obaja
 *
 */
public class CoordCourseDetailsServlet extends ActionServlet<CoordCourseDetailsHelper> {

	@Override
	protected CoordCourseDetailsHelper instantiateHelper() {
		return new CoordCourseDetailsHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseDetailsHelper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordCourseDetailsHelper helper) throws EntityDoesNotExistException{
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		
		// Process action
		if(courseID!=null){
			helper.course = helper.server.getCourseDetails(courseID);
			helper.students = helper.server.getStudentListForCourse(courseID);
		} else {
			helper.redirectUrl = Common.PAGE_COORD_COURSE;
		}
		
		sortStudents(helper.students);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_COURSE_DETAILS;
	}
}
