package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;

public class InstructorHomePageAction extends Action {
	
	private InstructorHomePageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		new GateKeeper().verifyInstructorPrivileges(account);
		
		data = new InstructorHomePageData(account);
		
		HashMap<String, CourseDetailsBundle> courses = logic.getCourseDetailsListForInstructor(account.googleId);
		
		data.courses = new ArrayList<CourseDetailsBundle>(courses.values());
		CourseDetailsBundle.sortDetailedCourses(data.courses);
		for(CourseDetailsBundle course: data.courses){
			EvaluationDetailsBundle.sortEvaluationsByDeadline(course.evaluations);
		}
		for(CourseDetailsBundle course: data.courses){
			FeedbackSessionDetailsBundle.sortFeedbackSessionsByCreationTime(course.feedbackSessions);
		}
		   
		statusToAdmin = "instructorHome Page Load<br>" + "Total Courses: " + data.courses.size();
		
		ShowPageResult response = createShowPageResult(Config.JSP_INSTRUCTOR_HOME, data);
		return response;

	}

}
