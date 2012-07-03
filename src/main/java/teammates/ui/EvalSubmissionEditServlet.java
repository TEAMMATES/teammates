package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.SubmissionData;
import teammates.jsp.EvalSubmissionEditHelper;

@SuppressWarnings("serial")
/**
 * Abstract servlet to handle submission editing.
 * This can be used as a student or as a coordinator, by implementing the
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
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		if(courseID==null || evalName==null){
			helper.redirectUrl = getDefaultRedirectUrl();
			return;
		}
		helper.student = getStudentObject(req, helper);
		if(helper.student==null){
			helper.statusMessage = getMessageOnNullStudent(req, helper);
			helper.error = true;
			helper.redirectUrl = getDefaultRedirectUrl();
			return;
		}
		helper.eval = helper.server.getEvaluation(courseID, evalName);
		try{
			helper.submissions = helper.server.getSubmissionsFromStudent(courseID, evalName, helper.student.email);
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			helper.redirectUrl = getDefaultRedirectUrl();
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
}
