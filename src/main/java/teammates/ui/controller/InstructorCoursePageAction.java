package teammates.ui.controller;

import java.util.ArrayList;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;

/**
 * Action: loading of the 'Courses' page for an instructor.
 */
public class InstructorCoursePageAction extends Action {
	protected static final Logger log = Config.getLogger();
	
	
	@Override
	public ActionResult execute() 
			throws EntityDoesNotExistException {
		
		new GateKeeper().verifyInstructorPrivileges(account);
		
		InstructorCoursePageData data = new InstructorCoursePageData(account);
		data.newCourse = null;
		data.instructorListToShow = account.googleId + "|" + account.name + "|" + account.email;
		data.courseIdToShow = "";
		data.courseNameToShow = "";
		
		data.currentCourses = new ArrayList<CourseDetailsBundle>(
				logic.getCourseSummariesForInstructor(account.googleId).values());
		CourseDetailsBundle.sortDetailedCoursesByCourseId(data.currentCourses);
		if (data.currentCourses.size() == 0 ){
			statusToUser.add(Config.MESSAGE_COURSE_EMPTY);
		}
		
		statusToAdmin = "instructorCourse Page Load<br>" 
				+ "Total courses: " + data.currentCourses.size();
		
		ShowPageResult response = createShowPageResult(Config.JSP_INSTRUCTOR_COURSE, data);
		return response;
	}


}
