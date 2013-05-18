package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

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
		String url = getRequestedURL(req);
		String servletName = "Unknown";
		String action = "Unknown";
		if(this instanceof InstructorEvalSubmissionEditHandlerServlet){
			servletName = Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_HANDLER_SERVLET;
			action = Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_HANDLER_SERVLET_EDIT_SUBMISSION;
		} else if (this instanceof StudentEvalEditHandlerServlet){
			servletName = Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET;
			action = Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET_EDIT_SUBMISSION;
		}
		
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String teamName = req.getParameter(Common.PARAM_TEAM_NAME);
		String fromEmail = req.getParameter(Common.PARAM_FROM_EMAIL);
		String[] toEmails = req.getParameterValues(Common.PARAM_TO_EMAIL);
		String[] points = req.getParameterValues(Common.PARAM_POINTS);
		String[] justifications = req.getParameterValues(Common.PARAM_JUSTIFICATION);
		String[] comments = req.getParameterValues(Common.PARAM_COMMENTS);
		
		EvaluationAttributes eval = helper.server.getEvaluation(courseID, evalName);
		
		ArrayList<SubmissionAttributes> submissionData = new ArrayList<SubmissionAttributes>();
		for(int i=0; i<toEmails.length; i++){
			SubmissionAttributes sub = new SubmissionAttributes();
			sub.course = courseID;
			sub.evaluation = evalName;
			sub.justification = new Text(justifications[i]);
			
			if (eval.p2pEnabled) {
				sub.p2pFeedback = new Text(comments[i]);
			}
			
			sub.points = Integer.parseInt(points[i]);
			sub.reviewee = toEmails[i];
			sub.reviewer = fromEmail;
			sub.team = teamName;
			submissionData.add(sub);
		}
		
		try{
			helper.server.updateSubmissions(submissionData);
			helper.statusMessage = getSuccessMessage(req,helper);
			helper.redirectUrl = getSuccessUrl();
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(courseID);
			data.add(evalName);
			data.add(teamName);
			data.add(fromEmail);
			data.add(toEmails);
			data.add(points);
			data.add(justifications);
			data.add(comments);			
			activityLogEntry = instantiateActivityLogEntry(servletName, action, true, helper, url, data);
			
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add(helper.statusMessage);	                        
	        activityLogEntry = instantiateActivityLogEntry(servletName, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
		}		
	}
}
