package teammates.jsp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import teammates.api.Common;
import teammates.api.InvalidParametersException;
import teammates.api.TeammatesException;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;

public class StudentHomeHelper extends Helper {
	public List<CourseData> courses;
	public String studentEmail;
	
	/**
	 * Returns the list of evaluation for a specific course.
	 * The evaluations are sorted by evaluation name
	 * @param course
	 * @return
	 */
	public static EvaluationData[] getEvaluationsForCourse(CourseData course){
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
		switch(eval.getStatus()){
		case PUBLISHED: return Common.STUDENT_EVALUATION_STATUS_PUBLISHED;
		case CLOSED: return Common.STUDENT_EVALUATION_STATUS_CLOSED;
		}
		boolean submitted = false;
		try {
			submitted = server.hasStudentSubmittedEvaluation(eval.course, eval.name, studentEmail);
		} catch (InvalidParametersException e) {
			System.err.println(TeammatesException.stackTraceToString(e));
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
		} else {
			return Common.HOVER_MESSAGE_STUDENT_EVALUATION_STATUS_PUBLISHED;
		}
	}
	
	/**
	 * Returns the link to see student's profile in a course
	 * @param courseID
	 * @return
	 */
	public String getStudentCourseProfileLink(String courseID){
		String link = Common.PAGE_STUDENT_COURSE_PROFILE;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to see course details, which includes the information
	 * about current student team
	 * @param courseID
	 * @return
	 */
	public String getStudentCourseDetailsLink(String courseID){
		String link = Common.PAGE_STUDENT_COURSE_DETAILS;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to see evaluation result
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getStudentEvaluationResultsLink(String courseID, String evalName){
		String link = Common.PAGE_STUDENT_EVAL_RESULTS;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_EVALUATION_NAME,evalName);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to submit or edit a submission for a specific evaluation.
	 * Note that the submit is essentially an edit to a blank submission.
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getStudentEvaluationSubmissionEditLink(String courseID, String evalName){
		String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_EVALUATION_NAME,evalName);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the list of available actions for a specific evaluation.
	 * @param eval
	 * @param idx
	 * @return
	 */
	public String getStudentEvaluationActions(EvaluationData eval, int idx){
		String studentStatus = getStudentStatusForEval(eval);
		if(studentStatus.equals(Common.STUDENT_EVALUATION_STATUS_PENDING)){
			return "<a id=\"doEvaluation" + idx + "\" " +
					"href=\"" + getStudentEvaluationSubmissionEditLink(eval.course,eval.name) + "\" " +
					"onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_STUDENT_EVALUATION_SUBMIT + "')\" " +
					"onmouseout=\"hideddrivetip()\">Submit</a>";
		}
		// CLOSED: No link is available
		boolean hasView = false;
		boolean hasEdit = false;
		if(studentStatus.equals(Common.STUDENT_EVALUATION_STATUS_PUBLISHED)){
			hasView = true;
		} else if(studentStatus.equals(Common.STUDENT_EVALUATION_STATUS_SUBMITTED)){
			hasEdit = true;
		}
		String result = "<a href=\"" + getStudentEvaluationResultsLink(eval.course,eval.name) + "\" " +
						"name=\"viewEvaluationResults" + idx + "\" " +
						" id=\"viewEvaluationResults" + idx + "\" " +
						"onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_EVALUATION_RESULTS + "')\" " +
						"onmouseout=\"hideddrivetip()\" " +
						(hasView ? "" : DISABLED) + ">" +
						"View Results</a>";
		result += "<a href=\"" + getStudentEvaluationSubmissionEditLink(eval.course,eval.name) + "\" " +
				"name=\"editEvaluationSubmission" + idx + "\" id=\"editEvaluationSubmission" + idx + "\" " +
				"onmouseover=\"ddrivetip('" + Common.HOVER_MESSAGE_EVALUATION_EDIT_SUBMISSION + "')\" onmouseout=\"hideddrivetip()\" " +
				(hasEdit ? "" : DISABLED) + ">Edit</a>";
		return result;
	}
}
