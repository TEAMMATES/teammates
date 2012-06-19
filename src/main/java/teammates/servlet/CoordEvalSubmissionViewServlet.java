package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.EvalResultData;
import teammates.jsp.CoordEvalSubmissionViewHelper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Evaluation Results action
 * @author Aldrian Obaja
 *
 */
public class CoordEvalSubmissionViewServlet extends ActionServlet<CoordEvalSubmissionViewHelper> {

	@Override
	protected CoordEvalSubmissionViewHelper instantiateHelper() {
		return new CoordEvalSubmissionViewHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordEvalSubmissionViewHelper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordEvalSubmissionViewHelper helper) throws EntityDoesNotExistException{
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		helper.byReviewer = !("false".equalsIgnoreCase(req.getParameter(Common.PARAM_BY_REVIEWER))); // Default to true
		
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
