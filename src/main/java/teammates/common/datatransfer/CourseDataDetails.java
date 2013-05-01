package teammates.common.datatransfer;

import java.util.ArrayList;

import teammates.common.Common;

public class CourseDataDetails {

	public CourseDataDetails(CourseData courseData) {
		this.course = courseData;
	}

	public CourseData course;
	public int teamsTotal = Common.UNINITIALIZED_INT;
	public int studentsTotal = Common.UNINITIALIZED_INT;
	public int unregisteredTotal = Common.UNINITIALIZED_INT;
	public ArrayList<EvaluationDataDetails> evaluations = new ArrayList<EvaluationDataDetails>();
	public ArrayList<TeamData> teams = new ArrayList<TeamData>();
	public ArrayList<StudentData> loners = new ArrayList<StudentData>();

}
