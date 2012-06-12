package teammates.jsp;

import java.util.ArrayList;

import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;

public class CoordHomeHelper extends Helper {
	public CourseData[] summary;
	
	public static EvaluationData[] getEvaluationsForCourse(CourseData course){
		ArrayList<EvaluationData> evaluations = course.evaluations;
		EvaluationData[] evaluationsArr = evaluations.toArray(new EvaluationData[]{});
		return evaluationsArr;
	}
}
