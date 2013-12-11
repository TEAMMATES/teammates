package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorCourseEditPageAction extends Action {
	protected static final Logger log = Utils.getLogger();
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException { 
				
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
		CourseAttributes courseToEdit = logic.getCourse(courseId);
		
		if (courseToEdit == null) {
			throw new EntityDoesNotExistException("Course "+courseId+" does not exist");
		} 
		
		new GateKeeper().verifyAccessible(instructor, courseToEdit);
		
		InstructorCourseEditPageData data = new InstructorCourseEditPageData(account);
		data.course = courseToEdit;
		data.instructorList = logic.getInstructorsForCourse(courseId);
		
		statusToAdmin = "instructorCourseEdit Page Load<br>"
				+ "Editing information for Course <span class=\"bold\">["
				+ courseId + "]</span>";
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, data);
		
	}

}
