package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.EvalResultData;

@SuppressWarnings("serial")
/**
 * Servlet to handle Evaluation Results action
 */
public class CoordEvalSubmissionViewServlet extends ActionServlet<CoordEvalSubmissionViewHelper> {

	@Override
	protected CoordEvalSubmissionViewHelper instantiateHelper() {
		return new CoordEvalSubmissionViewHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, CoordEvalSubmissionViewHelper helper) throws EntityDoesNotExistException{
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		if(courseID==null || evalName==null || studentEmail==null){
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
			return;
		}
		
		try {
			helper.student = helper.server.getStudent(courseID, studentEmail);
			helper.evaluation = helper.server.getEvaluation(courseID, evalName);
			helper.result = helper.server.getEvaluationResultForStudent(courseID, evalName, studentEmail);
		} catch (InvalidParametersException e) {
			helper.result = new EvalResultData();
			helper.statusMessage = e.getMessage();
			helper.error = true;
			return;
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_EVAL_SUBMISSION_VIEW;
	}
}
