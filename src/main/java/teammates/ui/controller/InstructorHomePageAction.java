package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorHomePageAction extends Action {
	
	private InstructorHomePageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		new GateKeeper().verifyInstructorUsingOwnIdOrAbove(account.googleId);
		
		data = new InstructorHomePageData(account);
		
		HashMap<String, CourseDetailsBundle> courses = logic.getCourseDetailsListForInstructor(account.googleId);
		
		data.courses = new ArrayList<CourseDetailsBundle>(courses.values());
		CourseDetailsBundle.sortDetailedCourses(data.courses);
		for(CourseDetailsBundle course: data.courses){
			EvaluationDetailsBundle.sortEvaluationsByDeadline(course.evaluations);
		}
		   
		statusToAdmin = "instructorHome Page Load<br>" + "Total Courses: " + data.courses.size();
		
		ShowPageResult response = createShowPageResult(Common.JSP_INSTRUCTOR_HOME, data);
		return response;

	}

}
