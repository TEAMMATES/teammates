package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorEvalsPageAction extends Action {
	Logger log = Utils.getLogger();

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		//This can be null. Non-null value indicates the page is being loaded 
		//   to add an evaluation to the specified course
		String courseIdForNewEvaluation = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		
		new GateKeeper().verifyInstructorPrivileges(account);
		
		if(courseIdForNewEvaluation!=null){
			new GateKeeper().verifyAccessible(
					logic.getInstructorForGoogleId(courseIdForNewEvaluation, account.googleId), 
					logic.getCourse(courseIdForNewEvaluation));
		}

		InstructorEvalPageData data = new InstructorEvalPageData(account);
		data.courseIdForNewEvaluation = courseIdForNewEvaluation;
		// This indicates that an empty form to be shown (except possibly the course value filled in)
		data.newEvaluationToBeCreated = null; 

		data.courses = loadCoursesList(account.googleId);
		if (data.courses.size() == 0) {
			statusToUser.add(Const.StatusMessages.COURSE_EMPTY_IN_EVALUATION.replace("${user}", "?user="+account.googleId));
			data.existingEvalSessions = new ArrayList<EvaluationAttributes>();
			data.existingFeedbackSessions = new ArrayList<FeedbackSessionAttributes>();
		
		} else {
			data.existingEvalSessions = loadEvaluationsList(account.googleId);			
			data.existingFeedbackSessions = loadFeedbackSessionsList(account.googleId);
			if (data.existingFeedbackSessions.isEmpty() &&
					data.existingEvalSessions.isEmpty()) {
				statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EMPTY);
			}
		}	
		EvaluationAttributes.sortEvaluationsByDeadlineDescending(data.existingEvalSessions);
		FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(data.existingFeedbackSessions);
		statusToAdmin = "Number of evaluations :"+data.existingEvalSessions.size();
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_EVALS, data);
	}

	protected List<FeedbackSessionAttributes> loadFeedbackSessionsList(
			String googleId) throws EntityDoesNotExistException {
		List<FeedbackSessionAttributes> sessions =
				logic.getFeedbackSessionsListForInstructor(googleId);
		
		return sessions;
	}
	
	protected List<EvaluationAttributes> loadEvaluationsList(String userId)
			throws EntityDoesNotExistException {
		List<EvaluationAttributes> evaluations =
				logic.getEvaluationsListForInstructor(userId);
		EvaluationAttributes.sortEvaluationsByDeadline(evaluations);

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