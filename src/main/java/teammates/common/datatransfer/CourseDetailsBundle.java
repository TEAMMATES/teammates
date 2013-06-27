package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents details of a course, including its students and evaluations.
 * <br> Contains:
 * <br> * statistics of teams, enrollments, registrations
 * <br> * Details of its evaluations (as {@link EvaluationDetailsBundle} objects)
 * <br> * Details of its feedback sessions (as {@link FeedbackSessionDetailsBundle} objects)
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
	public ArrayList<FeedbackSessionDetailsBundle> feedbackSessions = new ArrayList<FeedbackSessionDetailsBundle>();
	public ArrayList<TeamDetailsBundle> teams = new ArrayList<TeamDetailsBundle>();
	public ArrayList<StudentAttributes> loners = new ArrayList<StudentAttributes>();
	
	public static void sortDetailedCoursesByCourseId(List<CourseDetailsBundle> courses) {
		Collections.sort(courses, new Comparator<CourseDetailsBundle>() {
			public int compare(CourseDetailsBundle obj1, CourseDetailsBundle obj2) {
				return obj1.course.id.compareTo(obj2.course.id);
			}
		});
	}
	
	
	/**
	 * Sorts courses based on course ID
	 */ 
	public static void sortDetailedCourses(List<CourseDetailsBundle> courses) {
		Collections.sort(courses, new Comparator<CourseDetailsBundle>() {
			public int compare(CourseDetailsBundle obj1, CourseDetailsBundle obj2) {
				return obj1.course.id.compareTo(obj2.course.id);
			}
		});
	}

}
