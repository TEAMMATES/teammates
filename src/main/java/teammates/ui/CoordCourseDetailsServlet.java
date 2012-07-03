package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Coordinator View Course Details action
 *
 */
public class CoordCourseDetailsServlet extends ActionServlet<CoordCourseDetailsHelper> {

	@Override
	protected CoordCourseDetailsHelper instantiateHelper() {
		return new CoordCourseDetailsHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, CoordCourseDetailsHelper helper) throws EntityDoesNotExistException{
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		
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
