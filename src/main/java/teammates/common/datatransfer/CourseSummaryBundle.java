package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

public class CourseSummaryBundle {

    public CourseAttributes course;
    public ArrayList<FeedbackSessionAttributes> feedbackSessions = new ArrayList<FeedbackSessionAttributes>();

    public CourseSummaryBundle(CourseAttributes courseData) {
        this.course = courseData;
    }

    /**
     * Sorts courses based on course ID.
     */
    public static void sortSummarizedCoursesByCourseId(List<CourseSummaryBundle> courses) {
        Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
            @Override
            public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
                return obj1.course.getId().compareTo(obj2.course.getId());
            }
        });
    }

    /**
     * Sorts courses based on course name.
     */
    public static void sortSummarizedCoursesByCourseName(List<CourseSummaryBundle> courses) {
        Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
            @Override
            public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
                return obj1.course.getName().compareTo(obj2.course.getName());
            }
        });
    }

    /**
     * Sorts courses based on course creation date in the order of latest to oldest order.
     */
    public static void sortSummarizedCoursesByCreationDate(List<CourseSummaryBundle> courses) {
        Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
            @Override
            public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
                return obj2.course.createdAt.compareTo(obj1.course.createdAt);
            }
        });
    }

    public static void sortSummarizedCourses(List<CourseSummaryBundle> courses) {
        Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
            @Override
            public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
                return obj1.course.getId().compareTo(obj2.course.getId());
            }
        });
    }
}
