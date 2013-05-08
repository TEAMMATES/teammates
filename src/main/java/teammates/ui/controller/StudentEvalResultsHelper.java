package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;

public class StudentEvalResultsHelper extends Helper {
	public EvaluationAttributes eval;
	public StudentAttributes student;
	public List<SubmissionAttributes> incoming;
	public List<SubmissionAttributes> outgoing;
	public List<SubmissionAttributes> selfEvaluations;

	public StudentResultBundle evalResult;
	
	public static String colorizePoint(int points){
		return InstructorEvalResultsHelper.colorizePoints(points);
	}
	
	public static String getPointsListOriginal(List<SubmissionAttributes> subs){
		return InstructorEvalResultsHelper.getPointsList(subs, false);
	}
	
	public static String getPointsListNormalized(List<SubmissionAttributes> subs){
		return InstructorEvalResultsHelper.getPointsList(subs, true);
	}
	
	/**
	 * 
	 * @return Returns "normalized-for-student-view" points of the given 
	 *    submission list, sorted in descending order, formatted as a comma 
	 *    separated and colorized string. Excludes self-rating.
	 */
	public static String getNormalizedToStudentsPointsList(final List<SubmissionAttributes> subs){
		String result = "";
		List<SubmissionAttributes> tempSubs =  new ArrayList<SubmissionAttributes>(subs);

		Collections.sort(tempSubs, new Comparator<SubmissionAttributes>(){
			@Override
			public int compare(SubmissionAttributes s1, SubmissionAttributes s2){
				return Integer.valueOf(s2.normalizedToStudent).compareTo(s1.normalizedToStudent);
			}
		});
		for(SubmissionAttributes sub: tempSubs){
			if(sub.reviewee.equals(sub.reviewer)) continue;
			if(result!="") result+=", ";
			result+=InstructorEvalResultsHelper.colorizePoints(sub.normalizedToStudent);
		}
		return result;
	}

}