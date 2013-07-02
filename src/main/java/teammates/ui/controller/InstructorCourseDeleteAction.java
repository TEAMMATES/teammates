package teammates.ui.controller;

import java.util.ArrayList;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;

public class InstructorCourseDeleteAction extends InstructorCoursePageAction {
	
	protected static Logger log = Config.getLogger();
	

	public String idOfCourseToDelete;
	
	
	@Override
	public ActionResult execute()
			throws EntityDoesNotExistException {

		idOfCourseToDelete = getRequestParam(Config.PARAM_COURSE_ID);
		Assumption.assertNotNull(idOfCourseToDelete);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(idOfCourseToDelete, account.googleId), 
				logic.getCourse(idOfCourseToDelete));

		logic.deleteCourse(idOfCourseToDelete);
		statusToUser.add(Config.MESSAGE_COURSE_DELETED);
		statusToAdmin = "Course deleted: " + idOfCourseToDelete;

		InstructorCoursePageData data = new InstructorCoursePageData(account);
		data.newCourse = null;
		data.instructorListToShow = data.account.googleId + "|"	+ data.account.name + "|" + data.account.email;
		data.courseIdToShow = "";
		data.courseNameToShow = "";

		data.currentCourses = new ArrayList<CourseDetailsBundle>(
				logic.getCourseSummariesForInstructor(data.account.googleId).values());
		CourseDetailsBundle.sortDetailedCoursesByCourseId(data.currentCourses);

		ShowPageResult svr = createShowPageResult(Config.JSP_INSTRUCTOR_COURSE, data);
		return svr;

	}


}
