package teammates.jsp;

import java.util.List;

import teammates.datatransfer.EvalResultData;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.SubmissionData;

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