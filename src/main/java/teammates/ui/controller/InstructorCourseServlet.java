package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Add Course and Display Courses action
 */
public class InstructorCourseServlet extends ActionServlet<InstructorCourseHelper> {

	@Override
	protected InstructorCourseHelper instantiateHelper() {
		return new InstructorCourseHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorCourseHelper helper)
			throws EntityDoesNotExistException {
		
		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		helper.courseName = req.getParameter(Common.PARAM_COURSE_NAME);
		helper.instructorList = req.getParameter(Common.PARAM_COURSE_INSTRUCTOR_LIST);

		if (helper.courseID != null && helper.courseName != null && helper.courseName != null) {
			createCourse(helper);
		}

		HashMap<String, CourseData> courses = helper.server
				.getCourseListForInstructor(helper.userId);
		helper.courses = new ArrayList<CourseData>(courses.values());
		
		sortCourses(helper.courses);
		
		setStatus(helper);
	}

	private void createCourse(InstructorCourseHelper helper) {
		String courseIdBackup = helper.courseID;
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
		if(helper.error){
			return;
		}
		try{
			helper.server.updateCourseInstructors(courseIdBackup, helper.instructorList);
		} catch (InvalidParametersException e){
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

	//TODO: unit test this
	private void setStatus(InstructorCourseHelper helper) {
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
	
	private boolean noCoursesVisibleDueToEventualConsistency(InstructorCourseHelper helper) {
		return helper.statusMessage != null
				&& helper.statusMessage.equals(Common.MESSAGE_COURSE_ADDED)
				&& helper.courses.size()==0;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE;
	}
}
