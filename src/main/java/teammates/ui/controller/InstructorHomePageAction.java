package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorHomePageAction extends Action {
	
	private InstructorHomePageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		new GateKeeper().verifyInstructorPrivileges(account);
		
		data = new InstructorHomePageData(account);
		
		HashMap<String, CourseSummaryBundle> courses = logic.getCourseSummariesWithoutStatsForInstructor(account.googleId);
		
		data.courses = new ArrayList<CourseSummaryBundle>(courses.values());
		CourseSummaryBundle.sortSummarizedCourses(data.courses);
		for(CourseSummaryBundle course: data.courses){
			EvaluationAttributes.sortEvaluationsByDeadlineDescending(course.evaluations);
		}
		for(CourseSummaryBundle course: data.courses){
			FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(course.feedbackSessions);
		}
		   
		statusToAdmin = "instructorHome Page Load<br>" + "Total Courses: " + data.courses.size();
		
		ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME, data);
		return response;

	}

}
