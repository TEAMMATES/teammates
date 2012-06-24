package teammates.servlet;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.SubmissionData;
import teammates.jsp.Helper;

import com.google.appengine.api.datastore.Text;

@SuppressWarnings("serial")
public abstract class EvalSubmissionEditHandlerServlet extends ActionServlet<Helper> {
	
	/**
	 * The URL to redirect to in case of success
	 * @return
	 */
	protected abstract String getSuccessUrl();
	
	/**
	 * Returns the message to be displayed when the edit is successful
	 * @param req
	 * @param helper
	 * @return
	 */
	protected abstract String getSuccessMessage(HttpServletRequest req, Helper helper);

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}
	
	@Override
	protected void doAction(HttpServletRequest req, Helper helper)
			throws EntityDoesNotExistException {
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
			helper.statusMessage = getSuccessMessage(req,helper);
			helper.redirectUrl = getSuccessUrl();
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

}
