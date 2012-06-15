package teammates.jsp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import teammates.api.APIServlet;
import teammates.api.Common;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.UserData;

public class Helper {	
	/**
	 * The APIServlet object. Used to access the API.
	 */
	public APIServlet server;
	
	/**
	 * The user that is currently logged in, authenticated by Google
	 */
	public UserData user;
	
	/**
	 * The userID that the admin wants to masquerade
	 */
	public String requestedUser;
	
	/**
	 * The URL to forward to after finish processing this action
	 */
	public String nextUrl;
	
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
	
	public static final String DISABLED = "style=\"text-decoration:none; color:gray;\" onclick=\"return false\"";
	
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
		return (user.isAdmin)&&(requestedUser!=null);
	}
	
	/**
	 * Returns the URL with the specified key-value pair parameter added.
	 * Unchanged if either the key or value is null<br />
	 * Example:
	 * <ul>
	 * <li><code>addParam("index.jsp","action","add")</code> returns <code>index.jsp?action=add</code></li>
	 * <li><code>addParam("index.jsp?action=add","courseid","cs1101")</code> returns <code>index.jsp?action=add&courseid=cs1101</code></li>
	 * <li><code>addParam("index.jsp","message",null)</code> returns <code>index.jsp</code></li>
	 * </ul>
	 * @param url
	 * @param key
	 * @param value
	 * @return
	 * TODO Check for existing parameters
	 */
	public static String addParam(String url, String key, String value){
		if(key==null || value==null) return url;
		url += url.indexOf('?')>=0 ? '&' : '?';
		url += key+"="+convertForURL(value);
		return url;
	}
	
	/**
	 * Checks whether a name is longer than 20 characters,
	 * if so returns the truncated name appended by ellipsis,
	 * otherwise returns the original nickname.
	 * This is used for displaying nickname in the header, so that the
	 * nickname won't be too long that messes up with the header formatting.
	 * @param nickname
	 * @return
	 */
	public static String truncate(String nickname){
		String result = nickname;
		if(nickname.length()>20){
			result = nickname.substring(0,20)+"...";
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
	public static String escape(String str){
		return escapeHTML(str.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("'", "\\'")
				.replace("#", "\\#"));
	}

	/**
	 * Escape the string for inserting into HTML
	 * @param str
	 * @return
	 */
	public static String escapeHTML(String str){
		return str.replace("&", "&amp;")
				.replace("#", "&#35;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;")
				.replace("\\", "&#92;");
	}
	
	public static boolean isUserLoggedIn() {
		return APIServlet.isUserLoggedIn();
	}

	public static String getLoginUrl(HttpServletRequest request) {
		String queryString = request.getQueryString();
		String redirectUrl = request.getRequestURI()+(queryString!=null?"?"+queryString:"");
		return APIServlet.getLoginUrl(redirectUrl);
	}
	
	/**
	 * Returns the link to the course enroll link for specified courseID
	 * @param courseID
	 * @return
	 */
	public String getCoordCourseEnrollLink(String courseID){
		String link = Common.PAGE_COORD_COURSE_ENROLL;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}

	/**
	 * Returns the link to show course detail for specific courseID
	 * @param courseID
	 * @return
	 */
	public String getCoordCourseDetailsLink(String courseID){
		String link = Common.PAGE_COORD_COURSE_DETAILS;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID); 
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to delete a course and redirects to the nextURL after deletion<br />
	 * The nextURL is usually used to refresh the page after deletion
	 * @param courseID
	 * @param isHome
	 * @return
	 */
	public String getCoordCourseDeleteLink(String courseID, boolean isHome){
		String link = Common.PAGE_COORD_COURSE_DELETE;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_NEXT_URL,(isHome? Common.PAGE_COORD_HOME : Common.PAGE_COORD_COURSE));
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to delete an evaluation as specified and redirects to the nextURL after deletion<br />
	 * The nextURL is usually used to refresh the page after deletion
	 * @param courseID
	 * @param evalName
	 * @param nextURL
	 * @return
	 */
	public String getCoordEvaluationDeleteLink(String courseID, String evalName, String nextURL){
		String link = Common.PAGE_COORD_EVAL_DELETE;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = addParam(link,Common.PARAM_NEXT_URL,nextURL);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to edit an evaluation as specified
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getCoordEvaluationEditLink(String courseID, String evalName){
		String link = Common.PAGE_COORD_EVAL_EDIT;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_EVALUATION_NAME,evalName);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to see the result of an evaluation as specified
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getCoordEvaluationResultsLink(String courseID, String evalName){
		String link = Common.PAGE_COORD_EVAL_RESULTS;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_EVALUATION_NAME,evalName);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}

	/**
	 * Returns the link to see the result of an evaluation as specified
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getCoordEvaluationRemindLink(String courseID, String evalName){
		return "javascript: hideddrivetip(); toggleRemindStudents('"+courseID+"','"+evalName+"');";
	}
	
	/**
	 * Returns the link to publish or unpublish an evaluation, and redirects
	 * to appropriate page.
	 * @param courseID
	 * @param evalName
	 * @param publish
	 * 		true to publish, false to unpublish
	 * @param isHome
	 * @return
	 */
	public String getCoordEvaluationPublishLink(String courseID, String evalName, boolean publish, boolean isHome){
		return "javascript: hideddrivetip(); togglePublishEvaluation(" +
				"'" + courseID + "'," +
				"'" + evalName + "'," +
				publish + "," +
				"'" + (isHome? Common.PAGE_COORD_HOME : Common.PAGE_COORD_EVAL) +"');";
	}
	
	/**
	 * Returns the link to see submission details for a specified student in
	 * specified evaluation name and courseID
	 * @param courseID
	 * @param evalName
	 * @param studentEmail
	 * @return
	 */
	public String getCoordEvaluationSubmissionViewLink(String courseID, String evalName, String studentEmail){
		String link = Common.PAGE_COORD_EVAL_SUBMISSION_VIEW;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = addParam(link,Common.PARAM_STUDENT_EMAIL,studentEmail);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to edit submission details for a specified student in
	 * specified evaluation name and courseID
	 * @param courseID
	 * @param evalName
	 * @param studentEmail
	 * @return
	 */
	public String getCoordEvaluationSubmissionEditLink(String courseID, String evalName, String studentEmail){
		String link = Common.PAGE_COORD_EVAL_SUBMISSION_EDIT;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_EVALUATION_NAME,evalName);
		link = addParam(link,Common.PARAM_STUDENT_EMAIL,studentEmail);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
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
	public static String getCoordStatusForEval(EvaluationData eval){
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
	public static String getCoordHoverMessageForEval(EvaluationData eval){
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
	public String getCoordEvaluationActions(EvaluationData eval, int position, boolean isHome){
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
		}
		
		result.append(
			"<a class=\"t_eval_view\" name=\"viewEvaluation" + position + "\" id=\"viewEvaluation"+ position + "\" " +
			"href=\"" + getCoordEvaluationResultsLink(eval.course,eval.name) + "\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_RESULTS+"')\" "+
			"onmouseout=\"hideddrivetip()\"" + (hasView ? "" : DISABLED) + ">View Results</a>"
		);
		result.append(
			"<a class=\"t_eval_edit\" name=\"editEvaluation" + position + "\" id=\"editEvaluation" + position + "\" " +
			"href=\"" + getCoordEvaluationEditLink(eval.course,eval.name) + "\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_EDIT+"')\" onmouseout=\"hideddrivetip()\" " +
			(hasEdit ? "" : DISABLED) + ">Edit</a>"
		);
		result.append(
			"<a class=\"t_eval_delete\" name=\"deleteEvaluation" + position + "\" id=\"deleteEvaluation" + position + "\" " +
			"href=\"" + getCoordEvaluationDeleteLink(eval.course,eval.name,(isHome ? Common.PAGE_COORD_HOME : Common.PAGE_COORD_EVAL)) + "\" " +
			"onclick=\"hideddrivetip(); return toggleDeleteEvaluationConfirmation('" + eval.course + "','" + eval.name + "');\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_DELETE+"')\" onmouseout=\"hideddrivetip()\">Delete</a>"
		);
		result.append(
			"<a class=\"t_eval_remind\" name=\"remindEvaluation" + position + "\" id=\"remindEvaluation" + position + "\" " +
			"href=\"" + getCoordEvaluationRemindLink(eval.course,eval.name) + "\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_REMIND+"')\" " +
			"onmouseout=\"hideddrivetip()\"" + (hasRemind ? "" : DISABLED) + ">Remind</a>"
		);
		if (hasUnpublish) {
			result.append(
				"<a class=\"t_eval_unpublish\" name=\"unpublishEvaluation" + position + "\" id=\"publishEvaluation" + position + "\" " +
				"href=\"" + getCoordEvaluationPublishLink(eval.course,eval.name,false,isHome) + "\" " +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_UNPUBLISH+"')\" onmouseout=\"hideddrivetip()\">" +
				"Unpublish</a>"
			);
		} else {
			result.append(
				"<a class=\"t_eval_publish\" name=\"publishEvaluation" + position + "\" id=\"publishEvaluation" + position + "\" " +
				"href=\"" + getCoordEvaluationPublishLink(eval.course,eval.name,true,isHome) + "\" " +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_PUBLISH+"')\" " +
				"onmouseout=\"hideddrivetip()\"" + (hasPublish ? "" : DISABLED) + ">Publish</a>"
			);
		}
		return result.toString();
	}

}
