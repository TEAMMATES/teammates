package teammates.ui.controller;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;

/**
 * Data and utility methods needed to render a specific page.
 */
public class PageData {

	public static final String DISABLED = "style=\"color: gray; text-decoration: none;\" onclick=\"return false\"";

	/**
	 * @param account The account for the nominal user.
	 */
	public PageData(AccountAttributes account){
		this.account = account;
	}
	
	
	/** The user for whom the pages are displayed (i.e. the 'nominal user'). 
	 *    May not be the logged in user (under masquerade mode) */
	public AccountAttributes account;
	
	//====================== Utility Methods ===================================
	
	//TODO: move these helper method up/down the class hierarchy based on where they are used
	
	public String getInstructorCourseLink(){
		String link = Common.PAGE_INSTRUCTOR_COURSE;
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to the course enroll link for specified courseID<br />
	 * This includes masquerade mode as well.
	 * @return
	 */
	public String getInstructorCourseEnrollLink(String courseId){
		String link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseId);
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to save enroll info for given course<br />
	 * This includes masquerade mode as well.
	 * @return
	 */
	public String getInstructorCourseEnrollSaveLink(String courseId){
		//TODO: instead of using this method, the form should include these data as hidden fields?
		String link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL_SAVE;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseId);
		link = addUserIdToUrl(link);
		return link;
	}

	/**
	 * Returns the link to show course detail for specific courseID<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getInstructorCourseDetailsLink(String courseID){
		String link = Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID); 
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to edit course info for specific courseID<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getInstructorCourseEditLink(String courseID){
		String link = Common.PAGE_INSTRUCTOR_COURSE_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID); 
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to delete a course and redirects to the specific page
	 * after deletion depending on the isHome value<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param isHome
	 * @return
	 */
	public String getInstructorCourseDeleteLink(String courseID, boolean isHome){
		String link = Common.PAGE_INSTRUCTOR_COURSE_DELETE;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_NEXT_URL,(isHome? Common.PAGE_INSTRUCTOR_HOME : Common.PAGE_INSTRUCTOR_COURSE));
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to the instructor home link<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getInstructorHomeLink(){
		String link = Common.PAGE_INSTRUCTOR_HOME;
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to evaluation page<br />
	 * This includes masquerade mode as well.
	 * @return
	 */
	public String getInstructorEvaluationLink(){
		String link = Common.PAGE_INSTRUCTOR_EVAL;
		link = addUserIdToUrl(link);
		return link;
	}
	

	public String addUserIdToUrl(String link){
		return Common.addParamToUrl(link,Common.PARAM_USER_ID,account.googleId);
	}
	
	/**
	 * Escape the string for inserting into javascript code.
	 * This automatically calls {@link #escapeHTML} so that it is actually
	 * inserted correctly to the code.
	 * @param str
	 * @return
	 */
	public static String escapeForJavaScript(String str){
		return escapeForHTML(str.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("'", "\\'")
				.replace("#", "\\#"));
	}

	/**
	 * Escape the string for inserting into HTML
	 * @param str
	 * @return
	 */
	public static String escapeForHTML(String str){
		return str.replace("&", "&amp;")
				.replace("#", "&#35;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
	
	/**
	 * Returns the status of the student, whether he has joined the course.
	 * This is based on googleID, if it's null or empty, then we assume he
	 * has not joined the course yet.
	 * @param student
	 * @return
	 */
	public String status(StudentAttributes student){
		if(student.googleId == null || student.googleId.equals("")){
			return Common.STUDENT_STATUS_YET_TO_JOIN;
		} else {
			return Common.STUDENT_STATUS_JOINED;
		}
	}
	
	
	/**
	 * Returns the link to delete an evaluation as specified and redirects to the nextURL after deletion<br />
	 * The nextURL is usually used to refresh the page after deletion<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @param nextURL
	 * @return
	 */
	public String getInstructorEvaluationDeleteLink(String courseID, String evalName, String nextURL){
		String link = Common.PAGE_INSTRUCTOR_EVAL_DELETE;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = Common.addParamToUrl(link,Common.PARAM_NEXT_URL,addUserIdToUrl(nextURL));
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to edit an evaluation as specified<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getInstructorEvaluationEditLink(String courseID, String evalName){
		String link = Common.PAGE_INSTRUCTOR_EVAL_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to see the result of an evaluation as specified<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getInstructorEvaluationResultsLink(String courseID, String evalName){
		String link = Common.PAGE_INSTRUCTOR_EVAL_RESULTS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = addUserIdToUrl(link);
		return link;
	}

	/**
	 * Returns the link to send reminders to student who hasn't submit their
	 * evaluations<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getInstructorEvaluationRemindLink(String courseID, String evalName){
		String link = Common.PAGE_INSTRUCTOR_EVAL_REMIND;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID, courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to publish an evaluation, and redirects
	 * to appropriate page.<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @param isHome
	 * @return
	 */
	public String getInstructorEvaluationPublishLink(String courseID, String evalName, boolean isHome){
		String link = Common.PAGE_INSTRUCTOR_EVAL_PUBLISH;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID, courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = Common.addParamToUrl(link,Common.PARAM_NEXT_URL,(isHome ? addUserIdToUrl(Common.PAGE_INSTRUCTOR_HOME): addUserIdToUrl(Common.PAGE_INSTRUCTOR_EVAL)));
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to unpublish an evaluation, and redirects
	 * to appropriate page.<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @param isHome
	 * @return
	 */
	public String getInstructorEvaluationUnpublishLink(String courseID, String evalName, boolean isHome){
		String link = Common.PAGE_INSTRUCTOR_EVAL_UNPUBLISH;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID, courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = Common.addParamToUrl(link,Common.PARAM_NEXT_URL,(isHome ? addUserIdToUrl(Common.PAGE_INSTRUCTOR_HOME): addUserIdToUrl(Common.PAGE_INSTRUCTOR_EVAL)));
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to see submission details for a specified student in
	 * specified evaluation name and courseID<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @param studentEmail
	 * @return
	 */
	public String getInstructorEvaluationSubmissionViewLink(String courseID, String evalName, String studentEmail){
		String link = Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_VIEW;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,studentEmail);
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to edit submission details for a specified student in
	 * specified evaluation name and courseID<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @param evalName
	 * @param studentEmail
	 * @return
	 */
	public String getInstructorEvaluationSubmissionEditLink(String courseID, String evalName, String studentEmail){
		String link = Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,studentEmail);
		link = addUserIdToUrl(link);
		return link;
	}
	
	
	/**
	 * Returns the evaluaton status. Can be any one of these:
	 * <ul>
	 * <li>AWAITING - When the evaluation start time is still in the future</li>
	 * <li>OPEN - When the evaluation is started and has not passed the deadline</li>
	 * <li>CLOSED - When the evaluation deadline has passed but not published yet</li>
	 * <li>PUBLISHED - When the evaluation results has been published to students</li>
	 * </ul>
	 * @param eval
	 * @return
	 */
	public static String getInstructorStatusForEval(EvaluationAttributes eval){
		switch(eval.getStatus()){
		case AWAITING: return Common.EVALUATION_STATUS_AWAITING;
		case OPEN: return Common.EVALUATION_STATUS_OPEN;
		case CLOSED: return Common.EVALUATION_STATUS_CLOSED;
		default: return Common.EVALUATION_STATUS_PUBLISHED;
		}
	}
	
	/**
	 * Returns the hover message to explain evaluation status
	 * @param eval
	 * @return
	 */
	public static String getInstructorHoverMessageForEval(EvaluationAttributes eval){
		switch(eval.getStatus()){
		case AWAITING: return Common.HOVER_MESSAGE_EVALUATION_STATUS_AWAITING;
		case OPEN: return Common.HOVER_MESSAGE_EVALUATION_STATUS_OPEN;
		case CLOSED: return Common.HOVER_MESSAGE_EVALUATION_STATUS_CLOSED;
		default: return Common.HOVER_MESSAGE_EVALUATION_STATUS_PUBLISHED;
		}
	}
	
	/**
	 * Returns the links of actions available for a specific evaluation
	 * @param eval
	 * 		The Evaluation details
	 * @param position
	 * 		The position of the evaluation in the table (to be used for rowID)
	 * @param isHome
	 * 		Flag whether the link is to be put at homepage (to determine the redirect link in delete / publish)
	 * @return
	 */
	public String getInstructorEvaluationActions(EvaluationAttributes eval, int position, boolean isHome){
		StringBuffer result = new StringBuffer();
		
		boolean hasView = false;
		boolean hasEdit = false;
		boolean hasRemind = false;
		boolean hasPublish = false;
		boolean hasUnpublish = false;
		
		switch(eval.getStatus()){
		case AWAITING:
			hasEdit = true;
			break;
		case OPEN:
			hasView = true;
			hasEdit = true;
			hasRemind = true;
			break;
		case CLOSED:
			hasView = true;
			hasEdit = true;
			hasPublish = true;
			break;
		case PUBLISHED:
			hasUnpublish = true;
			hasView = true;
		}
		
		result.append(
			"<a class=\"color_green t_eval_view"+ position + "\" " +
			"href=\"" + getInstructorEvaluationResultsLink(eval.courseId,eval.name) + "\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_RESULTS+"')\" "+
			"onmouseout=\"hideddrivetip()\"" + (hasView ? "" : DISABLED) + ">View Results</a>"
		);
		result.append(
			"<a class=\"color_brown t_eval_edit" + position + "\" " +
			"href=\"" + getInstructorEvaluationEditLink(eval.courseId,eval.name) + "\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_EDIT+"')\" onmouseout=\"hideddrivetip()\" " +
			(hasEdit ? "" : DISABLED) + ">Edit</a>"
		);
		result.append(
			"<a class=\"color_red t_eval_delete" + position + "\" " +
			"href=\"" + getInstructorEvaluationDeleteLink(eval.courseId,eval.name,(isHome ? Common.PAGE_INSTRUCTOR_HOME : Common.PAGE_INSTRUCTOR_EVAL)) + "\" " +
			"onclick=\"hideddrivetip(); return toggleDeleteEvaluationConfirmation('" + eval.courseId + "','" + eval.name + "');\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_DELETE+"')\" onmouseout=\"hideddrivetip()\">Delete</a>"
		);
		result.append(
			"<a class=\"color_black t_eval_remind" + position + "\" " +
			"href=\"" + getInstructorEvaluationRemindLink(eval.courseId,eval.name) + "\" " +
			(hasRemind ? "onclick=\"hideddrivetip(); return toggleRemindStudents('" + eval.name + "');\" " : "") +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_REMIND+"')\" " +
			"onmouseout=\"hideddrivetip()\"" + (hasRemind ? "" : DISABLED) + ">Remind</a>"
		);
		if (hasUnpublish) {
			result.append(
				"<a class=\"color_black t_eval_unpublish" + position + "\" " +
				"href=\"" + getInstructorEvaluationUnpublishLink(eval.courseId,eval.name,isHome) + "\" " +
				"onclick=\"hideddrivetip(); return toggleUnpublishEvaluation('" + eval.name + "');\" " +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_UNPUBLISH+"')\" onmouseout=\"hideddrivetip()\">" +
				"Unpublish</a>"
			);
		} else {
			result.append(
				"<a class=\"color_black t_eval_publish" + position + "\" " +
				"href=\"" + getInstructorEvaluationPublishLink(eval.courseId,eval.name,isHome) + "\" " +
				(hasPublish ? "onclick=\"hideddrivetip(); return togglePublishEvaluation('" + eval.name + "');\" " : "") +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_PUBLISH+"')\" " +
				"onmouseout=\"hideddrivetip()\"" + (hasPublish ? "" : DISABLED) + ">Publish</a>"
			);
		}
		return result.toString();
	}
	


}
