package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorCourseDetailsPageAction extends Action {
	protected static final Logger log = Utils.getLogger();
	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException{
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		InstructorCourseDetailsPageData data = new InstructorCourseDetailsPageData(account);

		data.courseDetails = logic.getCourseDetails(courseId);
		data.students = logic.getStudentsForCourse(courseId);
		data.instructors = logic.getInstructorsForCourse(courseId);

		StudentAttributes.sortByNameAndThenByEmail(data.students);
		
		statusToAdmin = "instructorCourseDetails Page Load<br>" 
				+ "Viewing Course Details for Course <span class=\"bold\">[" + courseId + "]</span>";
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_DETAILS, data);
		
	}


}
