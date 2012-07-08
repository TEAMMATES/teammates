package teammates.ui.controller;

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
}