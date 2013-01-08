package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Instructor View Course Details action
 *
 */
public class InstructorCourseDetailsServlet extends ActionServlet<InstructorCourseDetailsHelper> {

	@Override
	protected InstructorCourseDetailsHelper instantiateHelper() {
		return new InstructorCourseDetailsHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorCourseDetailsHelper helper) throws EntityDoesNotExistException{
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		
		if(courseID!=null){
			helper.course = helper.server.getCourseDetails(courseID);
			helper.students = helper.server.getStudentListForCourse(courseID);
			helper.instructors = helper.server.getInstructorsByCourseId(courseID);
		} else {
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE;
		}
		
		sortStudents(helper.students);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_DETAILS;
	}
}
