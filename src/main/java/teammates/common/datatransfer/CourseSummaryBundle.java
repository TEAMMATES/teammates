package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.SanitizationHelper;

public class CourseSummaryBundle {

    public CourseAttributes course;
    public List<FeedbackSessionAttributes> feedbackSessions = new ArrayList<>();

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
                String courseName1 = obj1.course.getName();
                String courseName2 = obj2.course.getName();

                //TODO: [CourseAttribute] remove desanitization after data migration
                //desanitization is applied to course name to ensure a well-defined order of the courses by course name
                courseName1 = SanitizationHelper.desanitizeIfHtmlSanitized(courseName1);
                courseName2 = SanitizationHelper.desanitizeFromHtml(courseName2);

                return courseName1.compareTo(courseName2);
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
