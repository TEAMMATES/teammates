package teammates.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.SubmissionData;
import teammates.jsp.Helper;
import teammates.jsp.StudentEvalEditHelper;

@SuppressWarnings("serial")
public class StudentEvalEditServlet extends ActionServlet<StudentEvalEditHelper> {
	
	private static final String DISPLAY_URL = Common.JSP_STUDENT_EVAL_SUBMISSION_EDIT;

	@Override
	protected StudentEvalEditHelper instantiateHelper() {
		return new StudentEvalEditHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, StudentEvalEditHelper helper)
			throws IOException {
		if(!helper.user.isStudent && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, StudentEvalEditHelper helper)
			throws EntityDoesNotExistException {
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		if(courseID==null || evalName==null){
			helper.nextUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
		helper.student = helper.server.getStudentInCourseForGoogleId(courseID, helper.userId);
		if(helper.student==null){
			helper.statusMessage = "You are not registered in the course "+courseID;
			helper.error = true;
			helper.nextUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
		helper.eval = helper.server.getEvaluation(courseID, evalName);
		try{
			helper.submissions = helper.server.getSubmissionsFromStudent(courseID, evalName, helper.student.email);
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			helper.nextUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
		sortSubmissionsByReviewee(helper.submissions);
		
		// Put self submission at first
		for(int i=0; i<helper.submissions.size(); i++){
			SubmissionData sub = helper.submissions.get(i);
			if(sub.reviewee.equals(sub.reviewer)){
				helper.submissions.remove(sub);
				helper.submissions.add(0,sub);
				break;
			}
		}
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, StudentEvalEditHelper helper)
			throws ServletException, IOException {
		if(helper.nextUrl==null) helper.nextUrl = DISPLAY_URL;

		if(helper.nextUrl.startsWith(DISPLAY_URL)){
			// Goto display page
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(helper.nextUrl).forward(req, resp);
		} else {
			// Goto next page
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_STATUS_MESSAGE, helper.statusMessage);
			if(helper.error){
				helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_ERROR, ""+helper.error);
			}
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.requestedUser);
			resp.sendRedirect(helper.nextUrl);
		}
	}

}
