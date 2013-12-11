package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEvalResultsDownloadAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String evalName = getRequestParamValue(Const.ParamsNames.EVALUATION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getEvaluation(courseId, evalName));
		
		String fileContent = logic.getEvaluationResultSummaryAsCsv(courseId, evalName);
		String fileName = courseId + "_" + evalName;
		
		statusToAdmin = "Summary data for Evaluation "+ evalName + " in Course "+courseId + " was downloaded";
		
		return createFileDownloadResult(fileName, fileContent);
	}

}
