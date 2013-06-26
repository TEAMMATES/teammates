package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;

public class StudentHomePageData extends PageData {
	
	public StudentHomePageData(AccountAttributes account) {
		super(account);
	}
	
	public List<CourseDetailsBundle> courses = new ArrayList<CourseDetailsBundle>();
	public Map<String, String> evalSubmissionStatusMap = new HashMap<String, String>();
	
	
	/**
	 * Returns the submission status of the student for a given evaluation.
	 * The possible status are:
	 * <ul>
	 * <li>PENDING - The student has not submitted any submission</li>
	 * <li>SUBMITTED - The student has submitted a submission, and the evaluation is still open</li>
	 * <li>CLOSED - The evaluation has been closed (passed the deadline), and the result has not been published</li>
	 * <li>PUBLISHED - The evaluation is closed and the result is available for viewing</li>
	 * </ul>
	 */
	public String getStudentStatusForEval(EvaluationAttributes evaluation){
		return evalSubmissionStatusMap.get(evaluation.courseId+"%"+evaluation.name);
	}
	
	/**
	 * @param submissionStatus Submission status of a student for a particular evaluation. 
	 * Can be: PENDING, SUBMITTED, ClOSED, PUBLISHED.
	 * 
	 * @return The hover message to explain evaluation submission status.
	 */
	public String getStudentHoverMessageForEval(String submissionStatus){
		if(submissionStatus.equals(Common.STUDENT_EVALUATION_STATUS_PENDING)){
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_PENDING;
		} else if(submissionStatus.equals(Common.STUDENT_EVALUATION_STATUS_SUBMITTED)){
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_SUBMITTED;
		} else if(submissionStatus.equals(Common.STUDENT_EVALUATION_STATUS_CLOSED)){
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_CLOSED;
		} else if(submissionStatus.equals(Common.STUDENT_EVALUATION_STATUS_PUBLISHED)){
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_PUBLISHED;
		} else {
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_ERROR;
		}
	}
	
	
	
	public String getStudentCourseDetailsLink(String courseId){
		String link = Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseId);
		return link;
	}
	
	
	public String getStudentEvaluationResultsLink(String courseID, String evalName){
		String link = Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		return link;
	}
	
	/**
	 * Note that the submit is essentially an edit to a blank submission.<br />
	 * @return The link to submit or edit a submission for a specific evaluation.
	 */
	public String getStudentEvaluationSubmissionEditLink(String courseID, String evalName){
		String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		return link;
	}
	
	/**
	 * @return The list of available actions for a specific evaluation.
	 */
	public String getStudentEvaluationActions(EvaluationAttributes eval, int idx) {
		String studentStatus = getStudentStatusForEval(eval);
		
		if (studentStatus.equals(Common.STUDENT_EVALUATION_STATUS_PENDING)) {
			return "<a class=\"color_black\" id=\"submitEvaluation"
					+ idx
					+ "\" "
					+ "href=\""
					+ getStudentEvaluationSubmissionEditLink(eval.courseId,
							eval.name) + "\" " + "onmouseover=\"ddrivetip('"
					+ Common.HOVER_MESSAGE_STUDENT_EVALUATION_SUBMIT + "')\" "
					+ "onmouseout=\"hideddrivetip()\">Submit</a>";
		}
		
		
		boolean hasView = false;
		boolean hasEdit = false;
		if (studentStatus.equals(Common.STUDENT_EVALUATION_STATUS_PUBLISHED)) {
			hasView = true;
		} else if (studentStatus
				.equals(Common.STUDENT_EVALUATION_STATUS_SUBMITTED)) {
			hasEdit = true;
		} else if (studentStatus
				.equals(Common.STUDENT_EVALUATION_STATUS_CLOSED)) {
			hasEdit = true;
		}
		
		// @formatter:off
		String result = "<a class=\"color_black\" href=\""
				+ getStudentEvaluationResultsLink(eval.courseId, eval.name)
				+ "\" " + "name=\"viewEvaluationResults"
				+ idx + "\" " + " id=\"viewEvaluationResults" + idx + "\" "
				+ "onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_EVALUATION_RESULTS
				+ "')\" " + "onmouseout=\"hideddrivetip()\" " + (hasView ? "" : DISABLED)
				+ ">" + "View Results</a>" + "<a class=\"color_black\" href=\""
				+ getStudentEvaluationSubmissionEditLink(eval.courseId, eval.name)
				+ "\" " + "name=\"editEvaluationSubmission" + idx
				+ "\" id=\"editEvaluationSubmission" + idx + "\" "
				+ "onmouseover=\"ddrivetip('"
				+ Common.HOVER_MESSAGE_EVALUATION_EDIT_SUBMISSION
				+ "')\" onmouseout=\"hideddrivetip()\" "
				+ (hasEdit ? "" : DISABLED) + ">Edit/View Submission</a>";
		// @formatter:off
		
		return result;
	}

}
