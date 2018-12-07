package teammates.common.datatransfer;

import java.util.ArrayList;
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
        courses.sort(Comparator.comparing(obj -> obj.course.getId()));
    }

    /**
     * Sorts courses based on course name.
     */
    public static void sortSummarizedCoursesByCourseName(List<CourseSummaryBundle> courses) {
        //TODO: [CourseAttribute] remove desanitization after data migration
        //desanitization is applied to course name to ensure a well-defined order of the courses by course name
        courses.sort(Comparator.comparing(obj -> SanitizationHelper.desanitizeFromHtml(obj.course.getName())));
    }

    /**
     * Sorts courses based on course creation date in the order of latest to oldest order.
     */
    public static void sortSummarizedCoursesByCreationDate(List<CourseSummaryBundle> courses) {
        courses.sort(Comparator.comparing((CourseSummaryBundle obj) -> obj.course.createdAt).reversed());
    }

}
