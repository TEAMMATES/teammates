package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.EvalResultData;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;

public class StudentEvalResultsHelper extends Helper {
	public EvaluationData eval;
	public StudentData student;
	public List<SubmissionData> incoming;
	public List<SubmissionData> outgoing;
	public List<SubmissionData> selfEvaluations;

	public EvalResultData evalResult;
	
	public static String colorizePoint(int points){
		return CoordEvalResultsHelper.colorizePoints(points);
	}
	
	public static String getPointsListOriginal(List<SubmissionData> subs){
		return CoordEvalResultsHelper.getPointsList(subs, false);
	}
	
	public static String getPointsListNormalized(List<SubmissionData> subs){
		return CoordEvalResultsHelper.getPointsList(subs, true);
	}
	
	/**
	 * 
	 * @return Returns "normalized-for-student-view" points of the given 
	 *    submission list, sorted in descending order, formatted as a comma 
	 *    separated and colorized string. Excludes self-rating.
	 */
	public static String getNormalizedToStudentsPointsList(final List<SubmissionData> subs){
		String result = "";
		List<SubmissionData> temSubs =  new ArrayList<SubmissionData>(subs);

		Collections.sort(temSubs, new Comparator<SubmissionData>(){
			@Override
			public int compare(SubmissionData s1, SubmissionData s2){
				return Integer.valueOf(s2.normalizedToStudent).compareTo(s1.normalizedToStudent);
			}
		});
		for(SubmissionData sub: temSubs){
			if(sub.reviewee.equals(sub.reviewer)) continue;
			if(result!="") result+=", ";
			result+=CoordEvalResultsHelper.colorizePoints(sub.normalizedToStudent);
		}
		return result;
	}
	
}