package teammates.common.datatransfer;

import java.util.ArrayList;

import teammates.common.Common;

public class CourseDetailsBundle {

	public CourseDetailsBundle(CourseData courseData) {
		this.course = courseData;
	}

	public CourseData course;
	public int teamsTotal = Common.UNINITIALIZED_INT;
	public int studentsTotal = Common.UNINITIALIZED_INT;
	public int unregisteredTotal = Common.UNINITIALIZED_INT;
	public ArrayList<EvaluationDetailsBundle> evaluations = new ArrayList<EvaluationDetailsBundle>();
	public ArrayList<TeamDetailsBundle> teams = new ArrayList<TeamDetailsBundle>();
	public ArrayList<StudentData> loners = new ArrayList<StudentData>();

}
