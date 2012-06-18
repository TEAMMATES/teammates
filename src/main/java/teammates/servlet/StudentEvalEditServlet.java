package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.datatransfer.StudentData;
import teammates.jsp.EvalSubmissionEditHelper;
import teammates.jsp.StudentEvalEditHelper;

@SuppressWarnings("serial")
public class StudentEvalEditServlet extends EvalSubmissionEditServlet {

	@Override
	protected String getDisplayURL() {
		return Common.JSP_STUDENT_EVAL_SUBMISSION_EDIT;
	}

	@Override
	protected String getErrorPage() {
		return Common.PAGE_STUDENT_HOME;
	}

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
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, EvalSubmissionEditHelper helper)
			throws IOException {
		if(!helper.user.isStudent && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

}
