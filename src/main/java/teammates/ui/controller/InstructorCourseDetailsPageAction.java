package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorCourseDetailsPageAction extends Action {
	protected static final Logger log = Common.getLogger();
	
	
	@Override
	public ActionResult execute()
			throws EntityDoesNotExistException, InvalidParametersException {
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		new GateKeeper().verifyCourseInstructorOrAbove(courseId);
		
		InstructorCourseDetailsPageData data = new InstructorCourseDetailsPageData(account);

		data.courseDetails = logic.getCourseDetails(courseId);
		data.students = logic.getStudentsForCourse(courseId);
		data.instructors = logic.getInstructorsForCourse(courseId);

		StudentAttributes.sortByNameAndThenByEmail(data.students);
		
		statusToAdmin = "instructorCourseDetails Page Load<br>" 
				+ "Viewing Course Details for Course <span class=\"bold\">[" + courseId + "]</span>";
		
		return createShowPageResult(Common.JSP_INSTRUCTOR_COURSE_DETAILS, data);
		
	}


}
