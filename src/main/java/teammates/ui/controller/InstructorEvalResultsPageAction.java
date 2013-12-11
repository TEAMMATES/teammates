package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.api.GateKeeper;

public class InstructorEvalResultsPageAction extends Action {
	Logger log = Utils.getLogger();

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		InstructorEvalResultsPageData data = new InstructorEvalResultsPageData(account);
		
		data.evaluationResults = logic.getEvaluationResult(courseId, evalName);
		data.evaluationResults.sortForReportToInstructor();
				
		statusToUser.add(Const.StatusMessages.LOADING);
		statusToAdmin = "instructorEvalResults Page Load<br>" + "Viewing Results " +
				"for Evaluation <span class=\"bold\">" + evalName + "</span> " +
				"in Course <span class=\"bold\">[" + courseId + "]</span>";
			
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_EVAL_RESULTS, data);

	}

}