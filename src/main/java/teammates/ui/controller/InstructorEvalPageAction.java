package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.GateKeeper;

public class InstructorEvalPageAction extends Action {
	Logger log = Config.getLogger();

	@Override
	protected ActionResult execute() 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		//This can be null. Non-null value indicates the page is being loaded 
		//   to add an evaluation to the specified course
		String courseIdForNewEvaluation = getRequestParam(Const.ParamsNames.COURSE_ID);
		
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
			data.evaluations = new ArrayList<EvaluationDetailsBundle>();
		
		} else {
			data.evaluations = loadEvaluationsList(account.googleId);
			if (data.evaluations.size() == 0) {
				statusToUser.add(Const.StatusMessages.EVALUATION_EMPTY);
			}
		}
		
		statusToAdmin = "Number of evaluations :"+data.evaluations.size();
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_EVALS, data);
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