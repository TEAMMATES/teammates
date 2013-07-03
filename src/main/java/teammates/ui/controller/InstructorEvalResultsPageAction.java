package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class InstructorEvalResultsPageAction extends Action {
	Logger log = Config.getLogger();

	@Override
	protected ActionResult execute() 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		String courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		String evalName = getRequestParam(Constants.PARAM_EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		InstructorEvalResultsPageData data = new InstructorEvalResultsPageData(account);
		
		data.evaluationResults = logic.getEvaluationResult(courseId, evalName);
		data.evaluationResults.sortForReportToInstructor();
				
		statusToUser.add(Constants.STATUS_LOADING);
		statusToAdmin = "instructorEvalResults Page Load<br>" + "Viewing Results " +
				"for Evaluation <span class=\"bold\">" + evalName + "</span> " +
				"in Course <span class=\"bold\">[" + courseId + "]</span>";
			
		
		return createShowPageResult(Constants.VIEW_INSTRUCTOR_EVAL_RESULTS, data);

	}

}