package teammates.common.datatransfer;

import java.util.ArrayList;

/**
 * Represents details of a course, including its students and evaluations.
 * <br> Contains:
 * <br> * statistics of teams, enrollments, registrations
 * <br> * Details of its evaluations (as {@link EvaluationDetailsBundle} objects)
 * <br> * Details of its teams (as {@link TeamDetailsBundle} objects)
 * <br> * Details of students without teams i.e. 'loners'  (as {@link StudentAttributes} objects)
 *
 */
public class CourseDetailsBundle {

	public CourseDetailsBundle(CourseAttributes courseData) {
		this.course = courseData;
	}

	public CourseAttributes course;
	public CourseStats stats = new CourseStats();
	public ArrayList<EvaluationDetailsBundle> evaluations = new ArrayList<EvaluationDetailsBundle>();
	public ArrayList<TeamDetailsBundle> teams = new ArrayList<TeamDetailsBundle>();
	public ArrayList<StudentAttributes> loners = new ArrayList<StudentAttributes>();

}
