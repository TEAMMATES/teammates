package teammates.jsp;

import java.util.Arrays;
import java.util.Comparator;

import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;

public class CoordHomeHelper extends Helper {
	public CourseData[] courses;
	
	public static EvaluationData[] getEvaluationsForCourse(CourseData course){
		EvaluationData[] evaluationsArr = course.evaluations.toArray(new EvaluationData[]{});
		Arrays.sort(evaluationsArr, new Comparator<EvaluationData>(){
			public int compare(EvaluationData e1, EvaluationData e2){
				return e1.name.compareTo(e2.name);
			}
		});
		return evaluationsArr;
	}
}
