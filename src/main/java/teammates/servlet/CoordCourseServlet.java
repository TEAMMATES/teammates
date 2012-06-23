package teammates.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.CourseData;
import teammates.jsp.CoordCourseHelper;
import teammates.jsp.CoordEvalHelper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Add Course and Display Courses action
 */
public class CoordCourseServlet extends ActionServlet<CoordCourseHelper> {

	@Override
	protected CoordCourseHelper instantiateHelper() {
		return new CoordCourseHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseHelper helper)
			throws IOException {
		if (!helper.user.isCoord && !helper.user.isAdmin) {
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordCourseHelper helper)
			throws EntityDoesNotExistException {
		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		helper.courseName = req.getParameter(Common.PARAM_COURSE_NAME);

		if (helper.courseID != null && helper.courseName != null) {
			try {
				helper.server.createCourse(helper.userId, helper.courseID,
						helper.courseName);
				helper.courseID = null;
				helper.courseName = null;
				helper.statusMessage = Common.MESSAGE_COURSE_ADDED;
			} catch (EntityAlreadyExistsException e) {
				helper.statusMessage = Common.MESSAGE_COURSE_EXISTS;
				helper.error = true;
			} catch (InvalidParametersException e) {
				helper.statusMessage = e.getMessage();
				helper.error = true;
			}
		}

		HashMap<String, CourseData> courses = helper.server
				.getCourseListForCoord(helper.userId);
		helper.courses = new ArrayList<CourseData>(courses.values());
		//TODO: extract below logic and unit test it
		sortCourses(helper.courses);
		if (helper.courses.size() == 0
				&& !helper.error
				&& !noCoursesVisibleDueToEventualConsistency(helper)) {
			if (helper.statusMessage == null){
				helper.statusMessage = "";
			}else{
				helper.statusMessage += "<br />";
			}
			helper.statusMessage += Common.MESSAGE_COURSE_EMPTY;
		}
	}
	
	private boolean noCoursesVisibleDueToEventualConsistency(CoordCourseHelper helper) {
		return helper.statusMessage != null
				&& helper.statusMessage.equals(Common.MESSAGE_COURSE_ADDED)
				&& helper.courses.size()==0;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_COURSE;
	}
}
