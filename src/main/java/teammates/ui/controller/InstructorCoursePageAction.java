package teammates.ui.controller;

import java.util.ArrayList;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.GateKeeper;

/**
 * Action: loading of the 'Courses' page for an instructor.
 */
public class InstructorCoursePageAction extends Action {
	protected static final Logger log = Common.getLogger();
	
	
	@Override
	public ActionResult execute() 
			throws EntityDoesNotExistException {
		
		new GateKeeper().verifyInstructorUsingOwnIdOrAbove(account.googleId);
		
		InstructorCoursePageData data = new InstructorCoursePageData(account);
		data.newCourse = null;
		data.instructorListToShow = account.googleId + "|" + account.name + "|" + account.email;
		data.courseIdToShow = "";
		data.courseNameToShow = "";
		
		data.currentCourses = new ArrayList<CourseDetailsBundle>(
				logic.getCourseSummariesForInstructor(account.googleId).values());
		CourseDetailsBundle.sortDetailedCoursesByCourseId(data.currentCourses);
		if (data.currentCourses.size() == 0 ){
			statusToUser.add(Common.MESSAGE_COURSE_EMPTY);
		}
		
		statusToAdmin = "instructorCourse Page Load<br>" 
				+ "Total courses: " + data.currentCourses.size();
		
		ShowPageResult response = createShowPageResult(Common.JSP_INSTRUCTOR_COURSE, data);
		return response;
	}


}
