package teammates.ui.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.UserType;
import teammates.logic.api.Logic;

public class Helper {	
	/**
	 * The Logic object. Used to access the API.
	 */
	public Logic server;
	
	/**
	 * The user that is currently logged in, authenticated by Google
	 */
	public UserType user;
	
	/**
	 * AccountData which contains user's information
	 */
	public AccountData account;
	
	/**
	 * The userID that the admin wants to masquerade
	 */
	public String requestedUser;
	
	/**
	 * The URL to redirect to after finish processing the action
	 */
	public String redirectUrl;
	
	/**
	 * The URL to forward to after finish processing the action
	 */
	public String forwardUrl;
	
	/**
	 * The userID of the logged in user (<code>user.id</code>), or the userID
	 * requested by admin if in masquerade mode (<code>requestedUser</code>).
	 */
	public String userId;
	
	/**
	 * The status message that want to be displayed
	 */
	public String statusMessage = null;
	
	/**
	 * Flag whether there was an error, to be used to display status message style
	 * accordingly.
	 */
	public boolean error = false;
	
	public static final String DISABLED = "style=\"color: gray; text-decoration: none;\" onclick=\"return false\"";
	
	public Helper(){}
	
	public Helper(Helper helper){
		server = helper.server;
		user = helper.user;
		requestedUser = helper.requestedUser;
		userId = helper.userId;
		statusMessage = helper.statusMessage;
		error = helper.error;
	}

	public boolean isMasqueradeMode() {
		return (user!=null) && (requestedUser!=null);
	}
	
	/**
	 * Checks whether a name is longer than a specified length
	 * if so returns the truncated name appended by ellipsis,
	 * otherwise returns the original nickname.
	 * @param inputString
	 * @param truncateLength the maximum length of the truncated string
	 * @return
	 */
	public static String truncate(String inputString, int truncateLength){
		if(!(inputString.length()>truncateLength)){
			return inputString;
		}
		String result = inputString;
		if(inputString.length()>truncateLength){
			result = inputString.substring(0,truncateLength-3)+"...";
		}
		return result;
	}
	
	/**
	 * Converts a string to be put in URL (replaces some characters)
	 * @param str
	 * @return
	 */
	public static String convertForURL(String str){
		try {
			return URLEncoder.encode(str, Common.ENCODING);
		} catch (UnsupportedEncodingException e){
			return str;
		}
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
	
	public static boolean isUserLoggedIn() {
		return Logic.isUserLoggedIn();
	}

	public static String getLoginUrl(HttpServletRequest request) {
		String queryString = request.getQueryString();
		String redirectUrl = request.getRequestURI()+(queryString!=null?"?"+queryString:"");
		return Logic.getLoginUrl(redirectUrl);
	}
	
	/**
	 * Returns the link to the instructor home link<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getInstructorHomeLink(){
		String link = Common.PAGE_INSTRUCTOR_HOME;
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link to the student home link<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getStudentHomeLink(){
		String link = Common.PAGE_STUDENT_HOME;
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link to the course link<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getInstructorCourseLink(){
		String link = Common.PAGE_INSTRUCTOR_COURSE;
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link to the course enroll link for specified courseID<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getInstructorCourseEnrollLink(String courseID){
		String link = Common.PAGE_INSTRUCTOR_COURSE_ENROLL;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseID);
		link = processMasquerade(link);
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
		link = processMasquerade(link);
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
		link = processMasquerade(link);
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
		link = Common.addParamToUrl(link,Common.PARAM_NEXT_URL,(isHome? processMasquerade(Common.PAGE_INSTRUCTOR_HOME) : processMasquerade(Common.PAGE_INSTRUCTOR_COURSE)));
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link to evaluation page<br />
	 * This includes masquerade mode as well.
	 * @return
	 */
	public String getInstructorEvaluationLink(){
		String link = Common.PAGE_INSTRUCTOR_EVAL;
		link = processMasquerade(link);
		return link;
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
		link = Common.addParamToUrl(link,Common.PARAM_NEXT_URL,processMasquerade(nextURL));
		link = processMasquerade(link);
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
		link = processMasquerade(link);
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
		link = processMasquerade(link);
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
		link = processMasquerade(link);
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
		link = Common.addParamToUrl(link,Common.PARAM_NEXT_URL,(isHome ? processMasquerade(Common.PAGE_INSTRUCTOR_HOME): processMasquerade(Common.PAGE_INSTRUCTOR_EVAL)));
		link = processMasquerade(link);
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
		link = Common.addParamToUrl(link,Common.PARAM_NEXT_URL,(isHome ? processMasquerade(Common.PAGE_INSTRUCTOR_HOME): processMasquerade(Common.PAGE_INSTRUCTOR_EVAL)));
		link = processMasquerade(link);
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
		link = processMasquerade(link);
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
		link = processMasquerade(link);
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
	public static String getInstructorStatusForEval(EvaluationData eval){
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
	public static String getInstructorHoverMessageForEval(EvaluationData eval){
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
	public String getInstructorEvaluationActions(EvaluationData eval, int position, boolean isHome){
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
			"href=\"" + getInstructorEvaluationResultsLink(eval.course,eval.name) + "\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_RESULTS+"')\" "+
			"onmouseout=\"hideddrivetip()\"" + (hasView ? "" : DISABLED) + ">View Results</a>"
		);
		result.append(
			"<a class=\"color_brown t_eval_edit" + position + "\" " +
			"href=\"" + getInstructorEvaluationEditLink(eval.course,eval.name) + "\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_EDIT+"')\" onmouseout=\"hideddrivetip()\" " +
			(hasEdit ? "" : DISABLED) + ">Edit</a>"
		);
		result.append(
			"<a class=\"color_red t_eval_delete" + position + "\" " +
			"href=\"" + getInstructorEvaluationDeleteLink(eval.course,eval.name,(isHome ? Common.PAGE_INSTRUCTOR_HOME : Common.PAGE_INSTRUCTOR_EVAL)) + "\" " +
			"onclick=\"hideddrivetip(); return toggleDeleteEvaluationConfirmation('" + eval.course + "','" + eval.name + "');\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_DELETE+"')\" onmouseout=\"hideddrivetip()\">Delete</a>"
		);
		result.append(
			"<a class=\"color_black t_eval_remind" + position + "\" " +
			"href=\"" + getInstructorEvaluationRemindLink(eval.course,eval.name) + "\" " +
			(hasRemind ? "onclick=\"hideddrivetip(); return toggleRemindStudents('" + eval.name + "');\" " : "") +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_REMIND+"')\" " +
			"onmouseout=\"hideddrivetip()\"" + (hasRemind ? "" : DISABLED) + ">Remind</a>"
		);
		if (hasUnpublish) {
			result.append(
				"<a class=\"color_black t_eval_unpublish" + position + "\" " +
				"href=\"" + getInstructorEvaluationUnpublishLink(eval.course,eval.name,isHome) + "\" " +
				"onclick=\"hideddrivetip(); return toggleUnpublishEvaluation('" + eval.name + "');\" " +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_UNPUBLISH+"')\" onmouseout=\"hideddrivetip()\">" +
				"Unpublish</a>"
			);
		} else {
			result.append(
				"<a class=\"color_black t_eval_publish" + position + "\" " +
				"href=\"" + getInstructorEvaluationPublishLink(eval.course,eval.name,isHome) + "\" " +
				(hasPublish ? "onclick=\"hideddrivetip(); return togglePublishEvaluation('" + eval.name + "');\" " : "") +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_PUBLISH+"')\" " +
				"onmouseout=\"hideddrivetip()\"" + (hasPublish ? "" : DISABLED) + ">Publish</a>"
			);
		}
		return result.toString();
	}
	
	/**
	 * Returns the link with appended requested user ID if on masquerade mode
	 * @param link
	 * @return
	 */
	public String processMasquerade(String link){
		if(isMasqueradeMode()){
			return Common.addParamToUrl(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}

	/**
	 * Format P2P feedback
	 * Make the headings bold, and covert newlines to html linebreaks
	 * @return
	 */
	public static String formatP2PFeedback(String str, boolean enabled){
		if(!enabled){
			return "<span style=\"font-style: italic;\">Disabled</span>";
		}
		if(str.equals("") || str == null){
			return "N/A";
		}
		return str.replace("&lt;&lt;What I appreciate about you as a team member&gt;&gt;:", "<span class=\"bold\">What I appreciate about you as a team member:</span>")
				.replace("&lt;&lt;Areas you can improve further&gt;&gt;:", "<span class=\"bold\">Areas you can improve further:</span>")
				.replace("&lt;&lt;Other comments&gt;&gt;:", "<span class=\"bold\">Other comments:</span>")
				.replace("\n", "<br>");
	}
}
