package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class InstructorEvalResultsDownloadAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		String courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		String evalName = getRequestParam(Constants.PARAM_EVALUATION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		String fileContent = logic.getEvaluationResultSummaryAsCsv(courseId, evalName);
		String fileName = courseId + "_" + evalName;
		
		statusToAdmin = "Summary data for Evaluation "+ evalName + " in Course "+courseId + " was downloaded";
		
		return createFileDownloadResult(fileName, fileContent);
	}

}
