package teammates.jsp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import teammates.Common;
import teammates.jdo.EvaluationDetailsForCoordinator;

public class Helper {
	
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
			return URLEncoder.encode(str, "UTF8");
		} catch (UnsupportedEncodingException e){
			return str;
		}
	}
	
	/**
	 * Returns the link to show course detail for specific courseID
	 * @param courseID
	 * @return
	 */
	public static String getCourseDetailsLink(String courseID){
		return "coordCourseDetails.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID);
	}
	
	/**
	 * Returns the link to delete a course and redirects to the nextURL after deletion<br />
	 * The nextURL is usually used to refresh the page after deletion
	 * @param courseID
	 * @param nextURL
	 * @return
	 */
	public static String getCourseDeleteLink(String courseID, String nextURL){
		return "coordCourseDelete.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID)+"&"+Common.PARAM_NEXT_URL+"="+nextURL;
	}

	/**
	 * Returns the link to the course enroll link for specified courseID
	 * @param courseID
	 * @return
	 */
	public static String getCourseEnrollLink(String courseID){
		return "coordCourseEnroll.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID);
	}
	
	/**
	 * Returns the link to see evaluation details for a specified evaluation name and courseID
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public static String getEvaluationDetailsLink(String courseID, String evalName){
		return "coordEvalDetails.jsp?"+Common.PARAM_COURSE_ID+"="+courseID+"&"+Common.PARAM_EVALUATION_NAME+"="+evalName;
	}
	
	/**
	 * Returns the link to delete an evaluation as specified and redirects to the nextURL after deletion<br />
	 * The nextURL is usually used to refresh the page after deletion
	 * @param courseID
	 * @param evalName
	 * @param nextURL
	 * @return
	 */
	public static String getEvaluationDeleteLink(String courseID, String evalName, String nextURL){
		return "coordEvalDelete.jsp?"+Common.PARAM_COURSE_ID+"="+courseID+"&"+Common.PARAM_EVALUATION_NAME+"="+evalName+"&"+Common.PARAM_NEXT_URL+"="+convertForURL(nextURL);
	}
	
	/**
	 * Returns the link to edit an evaluation as specified
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public static String getEvaluationEditLink(String courseID, String evalName){
		return "coordEvalEdit.jsp?"+Common.PARAM_COURSE_ID+"="+courseID+"&"+Common.PARAM_EVALUATION_NAME+"="+evalName;
	}
	
	/**
	 * Returns the link to see the result of an evaluation as specified
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public static String getEvaluationResultsLink(String courseID, String evalName){
		return "coordEvalResults.jsp?"+Common.PARAM_COURSE_ID+"="+courseID+"&"+Common.PARAM_EVALUATION_NAME+"="+evalName;
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
	public static String getStatusForEval(EvaluationDetailsForCoordinator eval){
		if(eval.getStart().after(new Date())) return Common.EVALUATION_STATUS_AWAITING;
		if(eval.getDeadline().after(new Date())) return Common.EVALUATION_STATUS_OPEN;
		if(!eval.isPublished())	return Common.EVALUATION_STATUS_CLOSED;
		return Common.EVALUATION_STATUS_PUBLISHED;
	}
	
	/**
	 * Returns the hover message to explain evaluation status
	 * @param eval
	 * @return
	 */
	public static String getHoverMessageForEval(EvaluationDetailsForCoordinator eval){
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
	public static String getEvaluationActions(EvaluationDetailsForCoordinator eval, int position, boolean isHome){
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
			"<a class='t_eval_view' name='viewEvaluation" + position + "' id='viewEvaluation"+ position + "'" +
			"href=\"" + getEvaluationDetailsLink(eval.getCourseID(),eval.getName()) + "\"" +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_VIEW+"')\""+
			"onmouseout=\"hideddrivetip()\"" + (hasView ? "" : disabled) + ">View Results</a>"
		);
		result.append(
			"<a class='t_eval_edit' name='editEvaluation" + position + "' id='editEvaluation" + position + "'" +
			"href=\"" + getEvaluationEditLink(eval.getCourseID(),eval.getName()) + "\"" +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_EDIT+"')\" onmouseout=\"hideddrivetip()\"" +
			(hasEdit ? "" : disabled) + ">Edit</a>"
		);
		result.append(
			"<a class='t_eval_delete' name='deleteEvaluation" + position + "' id='deleteEvaluation" + position + "'" +
			"href=\"" + getEvaluationDeleteLink(eval.getCourseID(),eval.getName(),(isHome ? "coordHome.jsp" : "coordEval.jsp")) + "\"" +
			"onclick=\"hideddrivetip(); return toggleDeleteEvaluationConfirmation('" + eval.getCourseID() + "','" +
			eval.getName() + "');\"" +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_DELETE+"')\" onmouseout=\"hideddrivetip()\">Delete</a>"
		);
		result.append(
			"<a class='t_eval_remind' name='remindEvaluation" + position + "' id='remindEvaluation" + position + "'" +
			"href=\"javascript: hideddrivetip(); toggleRemindStudents('" + eval.getCourseID() + "','" + eval.getName() + "');\"" +
			"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_REMIND+"')\"" +
			"onmouseout=\"hideddrivetip()\"" + (hasRemind ? "" : disabled) + ">Remind</a>"
		);
		if (hasUnpublish) {
			result.append(
				"<a class='t_eval_unpublish' name='publishEvaluation" + position + "' id='publishEvaluation" + position + "'" +
				"href=\"javascript: hideddrivetip(); togglePublishEvaluation('" + eval.getCourseID() + "','" +
				eval.getName() + "'," + false + "," + (isHome ? "'coordHome.jsp'" : "'coordEval.jsp'") + ");\"" +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_UNPUBLISH+"')\" onmouseout=\"hideddrivetip()\">" +
				"Unpublish</a>"
			);
		} else {
			result.append(
				"<a class='t_eval_publish' name='unpublishEvaluation" + position + "' id='publishEvaluation" + position + "'" +
				"href=\"javascript: hideddrivetip(); togglePublishEvaluation('" + eval.getCourseID() + "','" +
				eval.getName() + "'," + true + "," + (isHome ? "'coordHome.jsp'" : "'coordEval.jsp'") + ");\"" +
				"onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_PUBLISH+"')\"" +
				"onmouseout=\"hideddrivetip()\"" + (hasPublish ? "" : disabled) + ">Publish</a>"
			);
		}
		return result.toString();
	}
}
