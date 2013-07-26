package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbacksPageAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		//This can be null. Non-null value indicates the page is being loaded 
		//   to add a feedback to the specified course
		String courseIdForNewSession = getRequestParam(Const.ParamsNames.COURSE_ID);
		
		new GateKeeper().verifyInstructorPrivileges(account);
				
		if(courseIdForNewSession!=null){
			new GateKeeper().verifyAccessible(
					logic.getInstructorForGoogleId(courseIdForNewSession, account.googleId), 
					logic.getCourse(courseIdForNewSession));
		}

		InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);
		data.courseIdForNewSession = courseIdForNewSession;
		// This indicates that an empty form to be shown (except possibly the course value filled in)
		data.newFeedbackSession = null; 

		data.courses = loadCoursesList(account.googleId);
		if (data.courses.size() == 0) {
			statusToUser.add(Const.StatusMessages.COURSE_EMPTY_IN_EVALUATION.replace("${user}", "?user="+account.googleId));
			data.existingEvalSessions = new ArrayList<EvaluationDetailsBundle>();
			data.existingFeedbackSessions = new ArrayList<FeedbackSessionDetailsBundle>();
		
		} else {
			data.existingEvalSessions = loadEvaluationsList(account.googleId);			
			data.existingFeedbackSessions = loadFeedbackSessionsList(account.googleId);
			if (data.existingFeedbackSessions.isEmpty() &&
				data.existingEvalSessions.isEmpty()) {
				statusToUser.add(Const.StatusMessages.EVALUATION_EMPTY);
			}
		}	
		
		statusToAdmin = "Number of feedback sessions: "+data.existingFeedbackSessions.size();
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
	}
	
	protected List<FeedbackSessionDetailsBundle> loadFeedbackSessionsList(
			String googleId) throws EntityDoesNotExistException {
		List<FeedbackSessionDetailsBundle> sessions =
				logic.getFeedbackSessionDetailsForInstructor(googleId);
		
		return sessions;
	}

	protected List<EvaluationDetailsBundle> loadEvaluationsList(String userId)
			throws EntityDoesNotExistException {
		List<EvaluationDetailsBundle> evaluations =
				logic.getEvaluationsDetailsForInstructor(userId);
		EvaluationDetailsBundle.sortEvaluationsByDeadline(evaluations);

		return evaluations;
	}
	
	protected List<CourseDetailsBundle> loadCoursesList(String userId)
			throws EntityDoesNotExistException {
		HashMap<String, CourseDetailsBundle> summary = 
				logic.getCourseSummariesForInstructor(userId);
		List<CourseDetailsBundle>courses = new ArrayList<CourseDetailsBundle>(summary.values());
		CourseDetailsBundle.sortDetailedCoursesByCourseId(courses);
		
		return courses;
	}

}
