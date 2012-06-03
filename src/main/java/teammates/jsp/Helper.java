package teammates.jsp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import teammates.api.Common;
import teammates.datatransfer.*;

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
			return URLEncoder.encode(str, Common.ENCODING);
		} catch (UnsupportedEncodingException e){
			return str;
		}
	}
	

	
	/**
	 * Returns the link to see evaluation details for a specified evaluation name and courseID
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public static String getEvaluationViewLink(String courseID, String evalName){
		return "coordEvalView.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID)+"&"+Common.PARAM_EVALUATION_NAME+"="+convertForURL(evalName);
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
		return "coordEvalDelete.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID)+"&"+Common.PARAM_EVALUATION_NAME+"="+convertForURL(evalName)+"&"+Common.PARAM_NEXT_URL+"="+convertForURL(nextURL);
	}
	
	/**
	 * Returns the link to edit an evaluation as specified
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public static String getEvaluationEditLink(String courseID, String evalName){
		return "coordEvalEdit.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID)+"&"+Common.PARAM_EVALUATION_NAME+"="+convertForURL(evalName);
	}
	
	/**
	 * Returns the link to see the result of an evaluation as specified
	 * @param courseID
	 * @param evalName
	 * @return
	 */
	public static String getEvaluationResultsLink(String courseID, String evalName){
		return "coordEvalResults.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID)+"&"+Common.PARAM_EVALUATION_NAME+"="+convertForURL(evalName);
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
		if(eval.endTime.after(new Date())) return Common.EVALUATION_STATUS_OPEN;
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
	public static String getEvaluationActions(EvaluationData eval, int position, boolean isHome){
		StringBuffer result = new StringBuffer();
		final String disabled = "style='text-decoration:none; color:gray;' onclick='return false'";
		
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
			"<a class='t_eval_view' name='viewEvaluation" + position + "' id='viewEvaluation"+ position + "' " +
			"href='" + getEvaluationViewLink(eval.course,eval.name) + "' " +
			"onmouseover='ddrivetip(\""+Common.HOVER_MESSAGE_EVALUATION_VIEW+"\")' "+
			"onmouseout='hideddrivetip()'" + (hasView ? "" : disabled) + ">View Results</a>"
		);
		result.append(
			"<a class='t_eval_edit' name='editEvaluation" + position + "' id='editEvaluation" + position + "' " +
			"href='" + getEvaluationEditLink(eval.course,eval.name) + "' " +
			"onmouseover='ddrivetip(\""+Common.HOVER_MESSAGE_EVALUATION_EDIT+"\")' onmouseout='hideddrivetip()' " +
			(hasEdit ? "" : disabled) + ">Edit</a>"
		);
		result.append(
			"<a class='t_eval_delete' name='deleteEvaluation" + position + "' id='deleteEvaluation" + position + "' " +
			"href='" + getEvaluationDeleteLink(eval.course,eval.name,(isHome ? "coordHome.jsp" : "coordEval.jsp")) + "' " +
			"onclick='hideddrivetip(); return toggleDeleteEvaluationConfirmation(\"" + eval.course + "\",\"" + eval.name + "\");' " +
			"onmouseover='ddrivetip(\""+Common.HOVER_MESSAGE_EVALUATION_DELETE+"\")' onmouseout='hideddrivetip()'>Delete</a>"
		);
		result.append(
			"<a class='t_eval_remind' name='remindEvaluation" + position + "' id='remindEvaluation" + position + "' " +
			"href='javascript: hideddrivetip(); toggleRemindStudents(\"" + eval.course + "\",\"" + eval.name + "\");' " +
			"onmouseover='ddrivetip(\""+Common.HOVER_MESSAGE_EVALUATION_REMIND+"\")' " +
			"onmouseout='hideddrivetip()'" + (hasRemind ? "" : disabled) + ">Remind</a>"
		);
		if (hasUnpublish) {
			result.append(
				"<a class='t_eval_unpublish' name='publishEvaluation" + position + "' id='publishEvaluation" + position + "' " +
				"href='javascript: hideddrivetip(); togglePublishEvaluation(\"" + eval.course + "\",\"" +
				eval.name + "\"," + false + "," + (isHome ? "\"coordHome.jsp\"" : "\"coordEval.jsp\"") + ");' " +
				"onmouseover='ddrivetip(\""+Common.HOVER_MESSAGE_EVALUATION_UNPUBLISH+"\")' onmouseout='hideddrivetip()'>" +
				"Unpublish</a>"
			);
		} else {
			result.append(
				"<a class='t_eval_publish' name='unpublishEvaluation" + position + "' id='publishEvaluation" + position + "' " +
				"href='javascript: hideddrivetip(); togglePublishEvaluation(\"" + eval.course + "\",\"" +
				eval.name + "\"," + true + "," + (isHome ? "\"coordHome.jsp\"" : "\"coordEval.jsp\"") + ");' " +
				"onmouseover='ddrivetip(\""+Common.HOVER_MESSAGE_EVALUATION_PUBLISH+"\")' " +
				"onmouseout='hideddrivetip()'" + (hasPublish ? "" : disabled) + ">Publish</a>"
			);
		}
		return result.toString();
	}
}
