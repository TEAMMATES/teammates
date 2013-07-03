package teammates.ui.controller;

import java.util.ArrayList;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class InstructorCourseDeleteAction extends InstructorCoursePageAction {
	
	protected static Logger log = Constants.getLogger();
	

	public String idOfCourseToDelete;
	
	
	@Override
	public ActionResult execute()
			throws EntityDoesNotExistException {

		idOfCourseToDelete = getRequestParam(Constants.PARAM_COURSE_ID);
		Assumption.assertNotNull(idOfCourseToDelete);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(idOfCourseToDelete, account.googleId), 
				logic.getCourse(idOfCourseToDelete));

		logic.deleteCourse(idOfCourseToDelete);
		statusToUser.add(Constants.STATUS_COURSE_DELETED);
		statusToAdmin = "Course deleted: " + idOfCourseToDelete;

		InstructorCoursePageData data = new InstructorCoursePageData(account);
		data.newCourse = null;
		data.instructorListToShow = data.account.googleId + "|"	+ data.account.name + "|" + data.account.email;
		data.courseIdToShow = "";
		data.courseNameToShow = "";

		data.currentCourses = new ArrayList<CourseDetailsBundle>(
				logic.getCourseSummariesForInstructor(data.account.googleId).values());
		CourseDetailsBundle.sortDetailedCoursesByCourseId(data.currentCourses);

		ShowPageResult svr = createShowPageResult(Constants.VIEW_INSTRUCTOR_COURSES, data);
		return svr;

	}


}
