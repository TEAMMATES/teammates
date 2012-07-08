package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;

@SuppressWarnings("serial")
/**
 * Servlet to handle Edit evaluation submission page.
 * This is for coordinator to edit student's submission
 */
public class CoordEvalSubmissionEditServlet extends EvalSubmissionEditServlet {

	@Override
	protected StudentData getStudentObject(HttpServletRequest req, EvalSubmissionEditHelper helper){
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		return helper.server.getStudent(courseID, studentEmail);
	}
	
	@Override
	protected String getMessageOnNullStudent(HttpServletRequest req,
			EvalSubmissionEditHelper helper) {
		return "There is no student with e-mail: " +
				EvalSubmissionEditHelper.escapeForHTML(req.getParameter(Common.PARAM_STUDENT_EMAIL)) +
				" registered in the course " + req.getParameter(Common.PARAM_COURSE_ID);
	}

	@Override
	protected CoordEvalSubmissionEditHelper instantiateHelper() {
		return new CoordEvalSubmissionEditHelper();
	}


	@Override
	protected String getDefaultRedirectUrl() {
		return Common.PAGE_COORD_EVAL;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_EVAL_SUBMISSION_EDIT;
	}
}
