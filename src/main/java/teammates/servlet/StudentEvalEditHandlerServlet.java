package teammates.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.SubmissionData;
import teammates.jsp.Helper;

import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public class StudentEvalEditHandlerServlet extends ActionServlet<Helper> {
	
	private static final String DISPLAY_URL = Common.PAGE_STUDENT_HOME;

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, Helper helper)
			throws IOException {
		if(!helper.user.isStudent && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper)
			throws EntityDoesNotExistException {
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String teamName = req.getParameter(Common.PARAM_TEAM_NAME);
		String fromEmail = req.getParameter(Common.PARAM_FROM_EMAIL);
		String[] toEmails = req.getParameterValues(Common.PARAM_TO_EMAIL);
		String[] points = req.getParameterValues(Common.PARAM_POINTS);
		String[] justifications = req.getParameterValues(Common.PARAM_JUSTIFICATION);
		String[] comments = req.getParameterValues(Common.PARAM_COMMENTS);
		
		ArrayList<SubmissionData> submissionData = new ArrayList<SubmissionData>();
		for(int i=0; i<toEmails.length; i++){
			SubmissionData sub = new SubmissionData();
			sub.course = courseID;
			sub.evaluation = evalName;
			sub.justification = new Text(justifications[i]);
			sub.p2pFeedback = new Text(comments[i]);
			sub.points = Integer.parseInt(points[i]);
			sub.reviewee = toEmails[i];
			sub.reviewer = fromEmail;
			sub.team = teamName;
			submissionData.add(sub);
		}
		
		try{
			helper.server.editSubmissions(submissionData);
			helper.statusMessage = String.format(Common.MESSAGE_EVALUATION_SUBMISSION_RECEIVED,evalName, courseID);
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, Helper helper)
			throws ServletException, IOException {
		if(helper.error){
			// Go back to edit page
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT).forward(req, resp);
		} else {
			// Goto next page
			helper.nextUrl = DISPLAY_URL;
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_STATUS_MESSAGE, helper.statusMessage);
			if(helper.error){
				helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_ERROR, ""+helper.error);
			}
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.requestedUser);
			resp.sendRedirect(helper.nextUrl);
		}
	}

}
