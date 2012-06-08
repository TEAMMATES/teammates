package teammates.jsp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import teammates.api.APIServlet;
import teammates.api.Common;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.UserData;

public class Helper {	
	/**
	 * The APIServlet object
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
	
	public static String escape(String str){
		return str.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
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
	public String getCourseEnrollLink(String courseID){
		String link = Common.JSP_COORD_COURSE_ENROLL;
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
	public String getCourseViewLink(String courseID){
		String link = Common.JSP_COORD_COURSE_DETAILS;
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
	public String getCourseDeleteLink(String courseID, boolean isHome){
		String link = Common.JSP_COORD_COURSE_DELETE;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_NEXT_URL,(isHome? Common.JSP_COORD_HOME : Common.JSP_COORD_COURSE));
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to see evaluation details for a specified evaluation name and courseID
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public String getEvaluationViewLink(String courseID, String evalName){
		String link = Common.JSP_COORD_EVAL_VIEW;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_EVALUATION_NAME,evalName);
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
	public String getEvaluationDeleteLink(String courseID, String evalName, String nextURL){
		String link = Common.JSP_COORD_EVAL_DELETE;
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
	public String getEvaluationEditLink(String courseID, String evalName){
		String link = Common.JSP_COORD_EVAL_EDIT;
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
	public String getEvaluationResultsLink(String courseID, String evalName){
		String link = Common.JSP_COORD_EVAL_RESULTS;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_EVALUATION_NAME,evalName);
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
	 * <li>CLOSED - When the evaluation deadline has passed</li>
	 * <li>PUBLISHED - When the evaluation results has been published to students</li>
	 * </ul>
	 * @param eval
	 * @return
	 */
	public static String getStatusForEval(EvaluationData eval){
		if(eval.startTime.after(new Date())) return Common.EVALUATION_STATUS_AWAITING;
		if(eval.endTime.after(new Date(
					new Date().getTime()+eval.gracePeriod*60*1000
				))
			) return Common.EVALUATION_STATUS_OPEN;
		if(!eval.published)	return Common.EVALUATION_STATUS_CLOSED;
		return Common.EVALUATION_STATUS_PUBLISHED;
	}
	
	/**
	 * Returns the hover message to explain evaluation status
	 * @param eval
	 * @return
	 */
	public static String getHoverMessageForEval(EvaluationData eval){
		String status = getStatusForEval(eval);
		if(status.equals(Common.EVALUATION_STATUS_AWAITING)) return Common.HOVER_MESSAGE_EVALUATION_STATUS_AWAITING;
		if(status.equals(Common.EVALUATION_STATUS_OPEN)) return Common.HOVER_MESSAGE_EVALUATION_STATUS_OPEN;
		if(status.equals(Common.EVALUATION_STATUS_CLOSED)) return Common.HOVER_MESSAGE_EVALUATION_STATUS_CLOSED;
		return Common.HOVER_MESSAGE_EVALUATION_STATUS_PUBLISHED;
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
	public String getEvaluationActions(EvaluationData eval, int position, boolean isHome){
		StringBuffer result = new StringBuffer();
		final String disabled = "style=\"text-decoration:none; color:gray;\" onclick=\"return false\"";
		
		boolean hasView = false;
		boolean hasEdit = false;
		boolean hasRemind = false;
		boolean hasPublish = false;
		boolean hasUnpublish = false;
		
		String status = getStatusForEval(eval);
		
		if(status.equals(Common.EVALUATION_STATUS_AWAITING)){
			hasEdit = true;
		} else if(status.equals(Common.EVALUATION_STATUS_OPEN)){
			hasView = true;
			hasEdit = true;
			hasRemind = true;
		} else if(status.equals(Common.EVALUATION_STATUS_CLOSED)){
			hasView = true;
			hasEdit = true;
			hasPublish = true;
		} else { // EVALUATION_STATUS_PUBLISHED
			hasView = true;
			hasUnpublish = true;
		}
		
		result.append(
			"<a class=\"t_eval_view\" name=\"viewEvaluation" + position + "\" id=\"viewEvaluation"+ position + "\" " +
			"href=\"" + getEvaluationViewLink(eval.course,eval.name) + "\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_VIEW+"')\" "+
			"onmouseout=\"hideddrivetip()\"" + (hasView ? "" : disabled) + ">View Results</a>"
		);
		result.append(
			"<a class=\"t_eval_edit\" name=\"editEvaluation" + position + "\" id=\"editEvaluation" + position + "\" " +
			"href=\"" + getEvaluationEditLink(eval.course,eval.name) + "\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_EDIT+"')\" onmouseout=\"hideddrivetip()\" " +
			(hasEdit ? "" : disabled) + ">Edit</a>"
		);
		result.append(
			"<a class=\"t_eval_delete\" name=\"deleteEvaluation" + position + "\" id=\"deleteEvaluation" + position + "\" " +
			"href=\"" + getEvaluationDeleteLink(eval.course,eval.name,(isHome ? Common.JSP_COORD_HOME : Common.JSP_COORD_EVAL)) + "\" " +
			"onclick=\"hideddrivetip(); return toggleDeleteEvaluationConfirmation('" + eval.course + "','" + eval.name + "');\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_DELETE+"')\" onmouseout=\"hideddrivetip()\">Delete</a>"
		);
		result.append(
			"<a class=\"t_eval_remind\" name=\"remindEvaluation" + position + "\" id=\"remindEvaluation" + position + "\" " +
			"href=\"javascript: hideddrivetip(); toggleRemindStudents('" + eval.course + "','" + eval.name + "');\" " +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_REMIND+"')\" " +
			"onmouseout=\"hideddrivetip()\"" + (hasRemind ? "" : disabled) + ">Remind</a>"
		);
		if (hasUnpublish) {
			result.append(
				"<a class=\"t_eval_unpublish\" name=\"publishEvaluation" + position + "\" id=\"publishEvaluation" + position + "\" " +
				"href=\"javascript: hideddrivetip(); togglePublishEvaluation('" + eval.course + "','" +
				eval.name + "'," + false + "," + (isHome ? "'"+Common.JSP_COORD_HOME+"'" : "'"+Common.JSP_COORD_EVAL+"'") + ");\" " +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_UNPUBLISH+"')\" onmouseout=\"hideddrivetip()\">" +
				"Unpublish</a>"
			);
		} else {
			result.append(
				"<a class=\"t_eval_publish\" name=\"unpublishEvaluation" + position + "\" id=\"publishEvaluation" + position + "\" " +
				"href=\"javascript: hideddrivetip(); togglePublishEvaluation('" + eval.course + "','" +
				eval.name + "'," + true + "," + (isHome ? "'"+Common.JSP_COORD_HOME+"'" : "'"+Common.JSP_COORD_EVAL+"'") + ");\" " +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_PUBLISH+"')\" " +
				"onmouseout=\"hideddrivetip()\"" + (hasPublish ? "" : disabled) + ">Publish</a>"
			);
		}
		return result.toString();
	}


}
