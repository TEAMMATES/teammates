package teammates.ui.controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.CourseDataDetails;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.InvalidParametersException;

public class StudentHomeHelper extends Helper {
	public List<CourseDataDetails> courses;
	
	/**
	 * Returns the list of evaluation for a specific course.
	 * The evaluations are sorted by evaluation name
	 * @param course
	 * @return
	 */
	public static EvaluationData[] getEvaluationsForCourse(CourseDataDetails course){
		EvaluationData[] result = course.evaluations.toArray(new EvaluationData[]{});
		Arrays.sort(result, new Comparator<EvaluationData>(){
			public int compare(EvaluationData e1, EvaluationData e2){
				return e1.name.compareTo(e2.name);
			}
		});
		return result;
	}
	
	/**
	 * Returns the student status of the given evaluation data.
	 * The possible status are:
	 * <ul>
	 * <li>PENDING - The student has not submitted any submission</li>
	 * <li>SUBMITTED - The student has submitted a submission, and the evaluation is still open</li>
	 * <li>CLOSED - The evaluation has been closed (passed the deadline), and the result has not been published</li>
	 * <li>PUBLISHED - The evaluation is closed and the result is available for viewing</li>
	 * </ul>
	 * Pre-condition: The given evaluation must not be in AWAITING state. 
	 * @param eval
	 * @return
	 */
	public String getStudentStatusForEval(EvaluationData eval){
		String studentEmail = null;
		StudentData student = server.getStudentInCourseForGoogleId(eval.course, userId);
		if(student!=null) studentEmail = student.email;
		switch(eval.getStatus()){
		case PUBLISHED: return Common.STUDENT_EVALUATION_STATUS_PUBLISHED;
		case CLOSED: return Common.STUDENT_EVALUATION_STATUS_CLOSED;
		}
		boolean submitted = false;
		try {
			submitted = server.hasStudentSubmittedEvaluation(eval.course, eval.name, studentEmail);
		} catch (InvalidParametersException e) {
			System.err.println(e.getMessage());
			return Common.STUDENT_EVALUATION_STATUS_ERROR;
		}
		if(submitted) return Common.STUDENT_EVALUATION_STATUS_SUBMITTED;
		else return Common.STUDENT_EVALUATION_STATUS_PENDING;
	}
	
	/**
	 * Returns the hover message to explain evaluation status
	 * @param eval
	 * @return
	 */
	public String getStudentHoverMessageForEval(EvaluationData eval){
		String status = getStudentStatusForEval(eval);
		if(status.equals(Common.STUDENT_EVALUATION_STATUS_PENDING)){
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_PENDING;
		} else if(status.equals(Common.STUDENT_EVALUATION_STATUS_SUBMITTED)){
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_SUBMITTED;
		} else if(status.equals(Common.STUDENT_EVALUATION_STATUS_CLOSED)){
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_CLOSED;
		} else if(status.equals(Common.STUDENT_EVALUATION_STATUS_PUBLISHED)){
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_PUBLISHED;
		} else {
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_ERROR;
		}
	}
	
	
	/**
	 * Returns the link to see course details, which includes the information
	 * about current student team<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getStudentCourseDetailsLink(String courseID){
		String link = Common.PAGE_STUDENT_COURSE_DETAILS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link to see evaluation result<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getStudentEvaluationResultsLink(String courseID, String evalName){
		String link = Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link to submit or edit a submission for a specific evaluation.
	 * Note that the submit is essentially an edit to a blank submission.<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getStudentEvaluationSubmissionEditLink(String courseID, String evalName){
		String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the list of available actions for a specific evaluation.<br />
	 * This includes masquerade mode as well.
	 * @param eval
	 * @param idx
	 * @return
	 */
	public String getStudentEvaluationActions(EvaluationData eval, int idx) {
		String studentStatus = getStudentStatusForEval(eval);
		
		if (studentStatus.equals(Common.STUDENT_EVALUATION_STATUS_PENDING)) {
			return "<a class=\"color_black\" id=\"submitEvaluation"
					+ idx
					+ "\" "
					+ "href=\""
					+ getStudentEvaluationSubmissionEditLink(eval.course,
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
				+ getStudentEvaluationResultsLink(eval.course, eval.name)
				+ "\" " + "name=\"viewEvaluationResults"
				+ idx + "\" " + " id=\"viewEvaluationResults" + idx + "\" "
				+ "onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_EVALUATION_RESULTS
				+ "')\" " + "onmouseout=\"hideddrivetip()\" " + (hasView ? "" : DISABLED)
				+ ">" + "View Results</a>" + "<a class=\"color_black\" href=\""
				+ getStudentEvaluationSubmissionEditLink(eval.course, eval.name)
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
