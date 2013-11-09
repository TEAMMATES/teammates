package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseSummaryBundle {
	
	public CourseSummaryBundle(CourseAttributes courseData) {
		this.course = courseData;
	}

	public CourseAttributes course;
	public ArrayList<EvaluationAttributes> evaluations = new ArrayList<EvaluationAttributes>();
	public ArrayList<FeedbackSessionAttributes> feedbackSessions = new ArrayList<FeedbackSessionAttributes>();
	public ArrayList<TeamDetailsBundle> teams = new ArrayList<TeamDetailsBundle>();
	public ArrayList<StudentAttributes> loners = new ArrayList<StudentAttributes>();
	
	public static void sortSummarizedCoursesByCourseId(List<CourseSummaryBundle> courses) {
		Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
			public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
				return obj1.course.id.compareTo(obj2.course.id);
			}
		});
	}
	
	
	/**
	 * Sorts courses based on course ID
	 */ 
	public static void sortSummarizedCourses(List<CourseSummaryBundle> courses) {
		Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
			public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
				return obj1.course.id.compareTo(obj2.course.id);
			}
		});
	}
}
