package teammates.servlet;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.jsp.EvalSubmissionEditHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
public class StudentEvalEditHandlerServlet extends EvalSubmissionEditHandlerServlet {

	@Override
	protected String getSuccessMessage(HttpServletRequest req, Helper helper){
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		return String.format(Common.MESSAGE_STUDENT_EVALUATION_SUBMISSION_RECEIVED,EvalSubmissionEditHelper.escapeForHTML(evalName), courseID);
	}

	@Override
	protected String getSuccessUrl() {
		return Common.PAGE_STUDENT_HOME;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_EVAL_SUBMISSION_EDIT;
	}

}
