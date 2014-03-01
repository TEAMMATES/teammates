package teammates.ui.controller;

import java.util.ArrayList;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorCourseDeleteAction extends InstructorCoursesPageAction {
	
	protected static Logger log = Utils.getLogger();
	
	@Override
	public ActionResult execute()
			throws EntityDoesNotExistException {

		String idOfCourseToDelete = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(idOfCourseToDelete);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(idOfCourseToDelete, account.googleId), 
				logic.getCourse(idOfCourseToDelete));

		logic.deleteCourse(idOfCourseToDelete);
		String statusMessage = String.format(Const.StatusMessages.COURSE_DELETED, idOfCourseToDelete);
		statusToUser.add(statusMessage);
		statusToAdmin = "Course deleted: " + idOfCourseToDelete;

		//TODO: Change to RedirectResult as mentioned in DevMan? This can avoid having to prepare page data
		InstructorCoursesPageData data = new InstructorCoursesPageData(account);
		data.newCourse = null;
		data.courseIdToShow = "";
		data.courseNameToShow = "";

		data.allCourses = new ArrayList<CourseDetailsBundle>(
				logic.getCourseSummariesForInstructor(data.account.googleId).values());
		data.archivedCourses = logic.getArchivedCoursesForInstructor(data.account.googleId);
		CourseDetailsBundle.sortDetailedCoursesByCourseId(data.allCourses);

		ShowPageResult svr = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSES, data);
		return svr;

	}


}
