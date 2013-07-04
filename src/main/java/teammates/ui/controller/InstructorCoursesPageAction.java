package teammates.ui.controller;

import java.util.ArrayList;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.GateKeeper;

/**
 * Action: loading of the 'Courses' page for an instructor.
 */
public class InstructorCoursesPageAction extends Action {
	protected static final Logger log = Utils.getLogger();
	
	
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
			statusToUser.add(Const.StatusMessages.COURSE_EMPTY);
		}
		
		statusToAdmin = "instructorCourse Page Load<br>" 
				+ "Total courses: " + data.currentCourses.size();
		
		ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data);
		return response;
	}


}
