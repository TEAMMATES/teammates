package teammates.common.datatransfer;

import java.util.ArrayList;

import teammates.common.Common;

public class CourseDataDetails {

	public CourseDataDetails(CourseData courseData) {
		this.course = courseData;
	}
	
	public CourseData course;
	public transient int teamsTotal = Common.UNINITIALIZED_INT;
	public transient int studentsTotal = Common.UNINITIALIZED_INT;
	public transient int unregisteredTotal = Common.UNINITIALIZED_INT;
	public transient ArrayList<EvaluationData> evaluations = new ArrayList<EvaluationData>();
	public transient ArrayList<TeamData> teams = new ArrayList<TeamData>();
	public transient ArrayList<StudentData> loners = new ArrayList<StudentData>();

}
