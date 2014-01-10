package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorStudentListPageAction extends Action {
	
	private InstructorStudentListPageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		new GateKeeper().verifyInstructorPrivileges(account);
		
		data = new InstructorStudentListPageData(account);
		
		HashMap<String, CourseDetailsBundle> courses = logic.getCourseDetailsListForInstructor(account.googleId);
		data.courses = new ArrayList<CourseDetailsBundle>(courses.values());
		CourseDetailsBundle.sortDetailedCourses(data.courses);
		
		if(data.courses.size() == 0){
			statusToUser.add(Const.StatusMessages.INSTRUCTOR_NO_COURSE_AND_STUDENTS);
		}
		   
		statusToAdmin = "instructorStudentList Page Load<br>" + "Total Courses: " + data.courses.size();
		
		ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, data);
		return response;

	}

}
