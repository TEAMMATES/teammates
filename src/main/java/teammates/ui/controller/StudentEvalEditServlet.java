package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;

@SuppressWarnings("serial")
public class StudentEvalEditServlet extends EvalSubmissionEditServlet {

	@Override
	protected StudentData getStudentObject(HttpServletRequest req,
			EvalSubmissionEditHelper helper) {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		return helper.server.getStudentInCourseForGoogleId(courseID, helper.userId);
	}
	
	@Override
	protected String getMessageOnNullStudent(HttpServletRequest req,
			EvalSubmissionEditHelper helper) {
		return "You are not registered in the course "+req.getParameter(Common.PARAM_COURSE_ID);
	}
	
	@Override
	protected StudentEvalEditHelper instantiateHelper() {
		return new StudentEvalEditHelper();
	}


	@Override
	protected String getDefaultRedirectUrl() {
		return Common.PAGE_STUDENT_HOME;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_EVAL_SUBMISSION_EDIT;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName,
			String action, boolean toShow, Helper helper) {
		// TODO Auto-generated method stub
		return null;
	}
}
