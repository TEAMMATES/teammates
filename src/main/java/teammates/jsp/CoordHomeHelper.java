package teammates.jsp;

import java.util.ArrayList;

import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;

public class CoordHomeHelper extends Helper {
	public String coordID;
	
	public CourseData[] summary;
	
	public CoordHomeHelper(Helper helper){
		super(helper);
	}
	
	public static EvaluationData[] getEvaluationsForCourse(CourseData course){
		ArrayList<EvaluationData> evaluations = course.evaluations;
		EvaluationData[] evaluationsArr = evaluations.toArray(new EvaluationData[]{});
		return evaluationsArr;
	}
}
