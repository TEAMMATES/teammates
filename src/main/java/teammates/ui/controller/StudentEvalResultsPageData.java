package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;

public class StudentEvalResultsPageData extends PageData {
	
	public StudentEvalResultsPageData(AccountAttributes account) {
		super(account);
	}
	
	public EvaluationAttributes eval;
	public StudentAttributes student;
	public List<SubmissionAttributes> incoming;
	public List<SubmissionAttributes> outgoing;
	public List<SubmissionAttributes> selfEvaluations;

	public StudentResultBundle evalResult;
	
	/**
	 * Method to color the points by adding <code>span</code> tag with appropriate
	 * class (posDiff and negDiff).
	 * Positive points will be green, negative will be red, 0 will be black.
	 * This will also put N/A or Not Sure for respective points representation.
	 * The output will be E+x% for positive points, E-x% for negative points,
	 * and just E for equal share.
	 * Zero contribution will be printed as 0%
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
	
	public static String getPointsListOriginal(List<SubmissionAttributes> subs){
		return getPointsList(subs, false);
	}
	
	public static String getPointsListNormalized(List<SubmissionAttributes> subs){
		return getPointsList(subs, true);
	}
	
	/**
	 * Prints the list of normalized points from the given list of submission data.
	 * It will be colorized and printed descending.
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
	
	/**
	 * @return The "normalized-for-student-view" points of the given 
	 *    submission list, sorted in descending order, formatted as a comma 
	 *    separated and colorized string. Excludes self-rating.
	 */
	public static String getNormalizedToStudentsPointsList(final List<SubmissionAttributes> subs){
		String result = "";
		List<SubmissionAttributes> tempSubs =  new ArrayList<SubmissionAttributes>(subs);

		Collections.sort(tempSubs, new Comparator<SubmissionAttributes>(){
			@Override
			public int compare(SubmissionAttributes s1, SubmissionAttributes s2){
				return Integer.valueOf(s2.details.normalizedToStudent).compareTo(s1.details.normalizedToStudent);
			}
		});
		for(SubmissionAttributes sub: tempSubs){
			if(sub.reviewee.equals(sub.reviewer)) continue;
			if(result!="") result+=", ";
			result+=InstructorEvalResultsHelper.colorizePoints(sub.details.normalizedToStudent);
		}
		return result;
	}
	
	/**
	 * Make the headings bold, and covert newlines to html linebreaks.
	 * @param isP2pEnabled Whether the P2P feedback is enabled.
	 */
	public static String formatP2PFeedback(String feedbackGiven, boolean isP2pEnabled){
		if(!isP2pEnabled){
			return "<span style=\"font-style: italic;\">Disabled</span>";
		}
		if(feedbackGiven.equals("") || feedbackGiven == null){
			return "N/A";
		}
		return feedbackGiven.replace("&lt;&lt;What I appreciate about you as a team member&gt;&gt;:", "<span class=\"bold\">What I appreciate about you as a team member:</span>")
				.replace("&lt;&lt;Areas you can improve further&gt;&gt;:", "<span class=\"bold\">Areas you can improve further:</span>")
				.replace("&lt;&lt;Other comments&gt;&gt;:", "<span class=\"bold\">Other comments:</span>")
				.replace("\n", "<br>");
	}

}
