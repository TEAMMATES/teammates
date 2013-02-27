package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;

@SuppressWarnings("serial")
public class InstructorEvalSubmissionEditHandlerServlet extends EvalSubmissionEditHandlerServlet {

	@Override
	protected String getSuccessMessage(HttpServletRequest req, Helper helper) {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String fromEmail = req.getParameter(Common.PARAM_FROM_EMAIL);
		StudentData student = helper.server.getStudent(courseID, fromEmail);
		String fromName;
		if(student==null) fromName = fromEmail;
		else fromName = student.name;
		return String.format(Common.MESSAGE_INSTRUCTOR_EVALUATION_SUBMISSION_RECEIVED,
				EvalSubmissionEditHelper.escapeForHTML(fromName),
				EvalSubmissionEditHelper.escapeForHTML(evalName), courseID);
	}


	@Override
	protected String getSuccessUrl() {
		return Common.JSP_SHOW_MESSAGE;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_SUBMISSION_EDIT;
	}


	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName,
			String action, boolean toShow, Helper helper) {
		// TODO Auto-generated method stub
		return null;
	}

}
