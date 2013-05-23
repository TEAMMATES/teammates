package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;

public class InstructorEvalResultsHelper extends Helper{
	public EvaluationResultsBundle evaluationResults;
	
	/**
	 * Returns the forward URL, which will be current page
	 * @return
	 */
	public String getForwardURL(){
		String link = Common.PAGE_INSTRUCTOR_EVAL_RESULTS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,evaluationResults.evaluation.courseId);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME, evaluationResults.evaluation.name);
		if(isMasqueradeMode()){
			link = Common.addParamToUrl(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Method to color the points by adding <code>span</code> tag with appropriate
	 * class (posDiff and negDiff).
	 * Positive points will be green, negative will be red, 0 will be black.
	 * This will also put N/A or Not Sure for respective points representation.
	 * The output will be E+x% for positive points, E-x% for negative points,
	 * and just E for equal share.
	 * Zero contribution will be printed as 0%
	 * @see #printSharePoints
	 * @param points
	 * 		In terms of full percentage, so equal share will be 100, 20% more
	 * 		from equal share will be 120, etc.
	 * @return
	 */
	public static String colorizePoints(int points){
		if(points==Common.POINTS_NOT_SUBMITTED || points==Common.UNINITIALIZED_INT)
			return "<span class=\"color_negative\" onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_SUBMISSION_NOT_AVAILABLE+"')\" onmouseout=\"hideddrivetip()\">N/A</span>";
		else if(points==Common.POINTS_NOT_SURE)
			return "<span class=\"color_negative\" onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_SUBMISSION_NOT_SURE+"')\" onmouseout=\"hideddrivetip()\">N/S</span>";
		else if(points==0)
			return "<span class=\"color_negative\">0%</span>";
		else if(points>100)
			return "<span class=\"color_positive\">E +"+(points-100)+"%</span>";
		else if(points<100)
			return "<span class=\"color_negative\">E -"+(100-points)+"%</span>";
		else
			return "<span class=\"color_neutral\">E</span>";
	}
	
	/**
	 * Method to color the points by adding <code>span</code> tag with appropriate
	 * class (posDiff and negDiff).
	 * Positive points will be green, negative will be red, 0 will be black.
	 * This will also put N/A or Not Sure for respective points representation.
	 * The output will be Equal Share + x% for positive points,
	 * Equal Share - x% for negative points,
	 * and just Equal Share for equal share.
	 * Zero contribution will be printed as 0%
	 * @see #colorizePoints
	 * @param points
	 * 		In terms of full percentage, so equal share will be 100, 20% more
	 * 		from equal share will be 120, etc.
	 * @param inline
	 * 		Whether or not the "Equal Share" and the percentage will be
	 * 		displayed in one line.
	 * @return
	 */
	public static String printSharePoints(int points, boolean inline){
		int delta = 0;
		if (points == Common.POINTS_NOT_SUBMITTED || points==Common.UNINITIALIZED_INT) {
			return "<span class=\"color_negative\">N/A</span>";
		} else if (points == Common.POINTS_NOT_SURE) {
			return "<span class=\"color_negative\">Not sure</span>";
		} else if (points == 0) {
			return "<span class=\"color_negative\">0%</span>";
		} else if (points > 100) {
			delta = points - 100;
			if(inline) return "Equal Share<span class=\"color_positive\"> + " + delta + "%</span>";
			else return "Equal Share<br /><span class=\"color_positive\"> + " + delta + "%</span>";
		} else if (points < 100) {
			delta = 100 - points;
			if(inline) return "Equal Share<span class=\"color_negative\"> - " + delta + "%</span>";
			else return "Equal Share<br /><span class=\"color_negative\"> - " + delta + "%</span>";
		} else {
			return "Equal Share";
		}
	}
	
	/**
	 * Prints the difference between Perceived and Claimed (normalized).
	 * Positive difference will be colored green, negative will be red, and
	 * neutral will be black.
	 * If any of the Perceived or Claimed is Not Available, the difference will
	 * be N/A, indicating not available.
	 * If any of the Perceived or Claimed is Not Sure, the difference will be
	 * N/S, indicating not sure.
	 * For each of the two special case above, a tooltip will be displayed to
	 * explain the meaning of the abbriviation.
	 * @param sub
	 * @return
	 */
	public static String printDiff(StudentResultBundle sub){
		int claimed = sub.summary.claimedToInstructor;
		int perceived = sub.summary.perceivedToInstructor;
		int diff = perceived - claimed;
		if(perceived==Common.POINTS_NOT_SUBMITTED || perceived==Common.UNINITIALIZED_INT
				|| claimed==Common.POINTS_NOT_SUBMITTED || claimed==Common.UNINITIALIZED_INT){
			return "<span class=\"negDiff\" onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_SUBMISSION_NOT_AVAILABLE+"')\" onmouseout=\"hideddrivetip()\">N/A</span>";
		} else if(perceived==Common.POINTS_NOT_SURE || claimed==Common.POINTS_NOT_SURE) {
			return "<span class=\"negDiff\" onmouseover=\"ddrivetip('"+Common.HOVER_MESSAGE_EVALUATION_SUBMISSION_NOT_SURE+"')\" onmouseout=\"hideddrivetip()\">N/S</span>";
		} else if(diff>0){
			return "<span class=\"posDiff\">+"+diff+"%</span>";
		} else if(diff<0){
			return "<span class=\"negDiff\">"+diff+"%</span>";
		} else {
			return "<span>"+diff+"</span>";
		}
	}
	
	/**
	 * Prints the justification from the given submission data.
	 * This will escape the string for HTML printing
	 * @param sub
	 * @return
	 */
	public static String printJustification(SubmissionAttributes sub){
		if(sub.justification==null || sub.justification.getValue()==null
				|| sub.justification.getValue().equals(""))
			return "N/A";
		else return escapeForHTML(sub.justification.getValue());
	}
	
	
	/**
	 * Prints the list of normalized points from the given list of submission data.
	 * It will be colorized and printed descending.
	 * @param subs
	 * @return
	 */
	public static String getPointsList(List<SubmissionAttributes> subs, final boolean normalized){
		//TODO: remove boolean variable and have two different variations of the method?
		String result = "";
		Collections.sort(subs, new Comparator<SubmissionAttributes>(){
			@Override
			public int compare(SubmissionAttributes s1, SubmissionAttributes s2){
				if(normalized)
					return Integer.valueOf(s2.details.normalizedToInstructor).compareTo(s1.details.normalizedToInstructor);
				else
					return Integer.valueOf(s2.points).compareTo(s1.points);
			}
		});
		for(SubmissionAttributes sub: subs){
			if(sub.reviewee.equals(sub.reviewer)) continue;
			if(result!="") result+=", ";
			if(normalized){
				result+=colorizePoints(sub.details.normalizedToInstructor);
			} else{
				result+=colorizePoints(sub.points);
			}
		}
		return result;
	}
	
}