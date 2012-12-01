package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.EvalResultData;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Evaluation Results action
 */
public class InstructorEvalSubmissionViewServlet extends ActionServlet<InstructorEvalSubmissionViewHelper> {

	@Override
	protected InstructorEvalSubmissionViewHelper instantiateHelper() {
		return new InstructorEvalSubmissionViewHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorEvalSubmissionViewHelper helper) throws EntityDoesNotExistException{
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		if(courseID==null || evalName==null || studentEmail==null){
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
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
		return Common.JSP_INSTRUCTOR_EVAL_SUBMISSION_VIEW;
	}
}
