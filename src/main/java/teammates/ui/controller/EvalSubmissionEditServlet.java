package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Abstract servlet to handle submission editing.
 * This can be used as a student or as a instructor, by implementing the
 * abstract methods here accordingly.
 * @author Aldrian Obaja
 *
 */
public abstract class EvalSubmissionEditServlet extends ActionServlet<EvalSubmissionEditHelper> {
	
	/**
	 * The URL to redirect to in case of error in the request
	 * @return
	 */
	protected abstract String getDefaultRedirectUrl();
	
	/**
	 * Returns the student object based on the request.
	 * Should return null when the student cannot be found in the specific course
	 * @param req
	 * @param helper
	 * 		Contains additional information that might be required
	 * @return
	 */
	protected abstract StudentData getStudentObject(HttpServletRequest req, EvalSubmissionEditHelper helper);
	
	/**
	 * The message to be displayed in case the {@link #getStudentObject) returns null
	 * @return
	 */
	protected abstract String getMessageOnNullStudent(HttpServletRequest req, EvalSubmissionEditHelper helper);

	@Override
	protected void doAction(HttpServletRequest req, EvalSubmissionEditHelper helper) throws EntityDoesNotExistException{
		String url = getRequestedURL(req);
		String servletName = "Unknown";
		String action = "Unknown";
		if(this instanceof InstructorEvalSubmissionEditServlet){
			servletName = Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SERVLET;
			action = Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SERVLET_PAGE_LOAD;
		} else if (this instanceof StudentEvalEditServlet){
			servletName = Common.STUDENT_EVAL_EDIT_SERVLET;
			action = Common.STUDENT_EVAL_EDIT_SERVLET_PAGE_LOAD;
		}
		
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		if(courseID==null || evalName==null){
			helper.redirectUrl = getDefaultRedirectUrl();
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add("Course Id or Evaluation name is null");						
			activityLogEntry = instantiateActivityLogEntry(servletName, Common.LOG_SERVLET_ACTION_FAILURE,
					true, helper, url, data);
			return;
		}
		
		helper.student = getStudentObject(req, helper);
		
		if(helper.student==null){
			helper.statusMessage = getMessageOnNullStudent(req, helper);
			helper.error = true;
			helper.redirectUrl = getDefaultRedirectUrl();
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.statusMessage);					
			activityLogEntry = instantiateActivityLogEntry(servletName, Common.LOG_SERVLET_ACTION_FAILURE,
					true, helper, url, data);
			return;
		}
		
		helper.eval = helper.server.getEvaluation(courseID, evalName);
		
		try{
			helper.submissions = helper.server.getSubmissionsFromStudent(courseID, evalName, helper.student.email);
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(courseID);
			data.add(evalName);
			data.add(helper.student.name);
			activityLogEntry = instantiateActivityLogEntry(servletName, action, true, helper, url, data);
			
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			helper.redirectUrl = getDefaultRedirectUrl();
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.statusMessage);			
			activityLogEntry = instantiateActivityLogEntry(servletName, Common.LOG_SERVLET_ACTION_FAILURE,
					true, helper, url, data);
		}
		sortSubmissionsByReviewee(helper.submissions);
		
		// Put self submission at first
		for(int i=0; i<helper.submissions.size(); i++){
			SubmissionAttributes sub = helper.submissions.get(i);
			if(sub.reviewee.equals(sub.reviewer)){
				helper.submissions.remove(sub);
				helper.submissions.add(0,sub);
				break;
			}
		}
	}
}
