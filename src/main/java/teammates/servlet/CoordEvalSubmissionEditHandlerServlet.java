package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.StudentData;
import teammates.jsp.EvalSubmissionEditHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
public class CoordEvalSubmissionEditHandlerServlet extends EvalSubmissionEditHandlerServlet {

	@Override
	protected String getSuccessMessage(HttpServletRequest req, Helper helper) {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String fromEmail = req.getParameter(Common.PARAM_FROM_EMAIL);
		StudentData student = helper.server.getStudent(courseID, fromEmail);
		String fromName;
		if(student==null) fromName = fromEmail;
		else fromName = student.name;
		return String.format(Common.MESSAGE_COORD_EVALUATION_SUBMISSION_RECEIVED,
				EvalSubmissionEditHelper.escapeHTML(fromName),
				EvalSubmissionEditHelper.escapeHTML(evalName), courseID);
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, Helper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String studentEmail = req.getParameter(Common.PARAM_FROM_EMAIL);
		CourseData course = helper.server.getCourse(courseID);
		if(course!=null && !course.coord.equals(helper.userId)){
			helper.statusMessage = "You are not authorized to edit the submission for student " +
					Helper.escapeHTML(studentEmail)+" in evaluation "+Helper.escapeHTML(evalName) +
					" in course "+courseID;
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
			return false;
		}
		return true;
	}

	@Override
	protected String getSuccessUrl() {
		return Common.JSP_SHOW_MESSAGE;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_EVAL_SUBMISSION_EDIT;
	}

}
