package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.datatransfer.StudentData;
import teammates.jsp.EvalSubmissionEditHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
public class StudentEvalEditHandlerServlet extends EvalSubmissionEditHandlerServlet {

	@Override
	protected String getSuccessMessage(HttpServletRequest req, Helper helper){
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		return String.format(Common.MESSAGE_EVALUATION_SUBMISSION_RECEIVED,EvalSubmissionEditHelper.escapeHTML(evalName), courseID);
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, Helper helper)
			throws IOException {
		if(!helper.user.isStudent && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_FROM_EMAIL);
		if(studentEmail==null) return true;
		StudentData student = helper.server.getStudentInCourseForGoogleId(courseID, helper.userId);
		if(student!=null && !student.email.equals(studentEmail)){
			helper.statusMessage = "You are only allowed to edit your own submission";
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			return false;
		}
		return true;
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
