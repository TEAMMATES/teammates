package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.StudentData;
import teammates.jsp.CoordEvalSubmissionEditHelper;
import teammates.jsp.EvalSubmissionEditHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Edit evaluation submission page.
 * This is for coordinator to edit student's submission
 * @author Aldrian Obaja
 *
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
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, EvalSubmissionEditHelper helper)
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
					Helper.escapeForHTML(studentEmail)+" in evaluation "+Helper.escapeForHTML(evalName) +
					" in course "+courseID;
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
			return false;
		}
		return true;
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
