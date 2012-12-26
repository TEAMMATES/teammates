package teammates.ui.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
public class StudentEvalEditServlet extends EvalSubmissionEditServlet {

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
	protected String getDefaultRedirectUrl() {
		return Common.PAGE_STUDENT_HOME;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_EVAL_SUBMISSION_EDIT;
	}

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
		
		Date currentDate = new Date();
		if(currentDate.compareTo(helper.eval.endTime) > 0){
			helper.statusMessage = Common.MESSAGE_EVALUATION_EXPIRED;
			helper.error = true;
			helper.redirectUrl = getDefaultRedirectUrl();
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
