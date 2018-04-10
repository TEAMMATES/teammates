package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.SanitizationHelper;

/**
 * Represents details of a course, including its students and feedback sessions.
 * <br> Contains:
 * <br> * statistics of teams, enrollments, registrations
 * <br> * Details of its feedback sessions (as {@link FeedbackSessionDetailsBundle} objects)
 * <br> * Details of its teams (as {@link TeamDetailsBundle} objects)
 *
 */
public class CourseDetailsBundle {
    public CourseAttributes course;
    public CourseStats stats = new CourseStats();

    public List<FeedbackSessionDetailsBundle> feedbackSessions = new ArrayList<>();
    public List<SectionDetailsBundle> sections = new ArrayList<>();

    public CourseDetailsBundle(CourseAttributes courseData) {
        this.course = courseData;
        //TODO: [CourseAttribute] remove desanitization after data migration
        //creating a new course with possibly desanitized name as course name cannot be accessed directly
        this.course = CourseAttributes
                .builder(courseData.getId(),
                        SanitizationHelper.desanitizeIfHtmlSanitized(courseData.getName()),
                        courseData.getTimeZone())
                .build();
        this.course.createdAt = courseData.createdAt;
    }

    /**
     * Gets all FeedbackSessionAttributes in this CourseDetailsBundle.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsList() {
        List<FeedbackSessionAttributes> feedbackSessionAttributes = new ArrayList<>();
        for (FeedbackSessionDetailsBundle feedbackSessionDetails : feedbackSessions) {
            feedbackSessionAttributes.add(feedbackSessionDetails.feedbackSession);
        }
        return feedbackSessionAttributes;
    }

    /**
     * Sorts courses based on course ID.
     */
    public static void sortDetailedCoursesByCourseId(List<CourseDetailsBundle> courses) {
        courses.sort(Comparator.comparing(obj -> obj.course.getId()));
    }

    /**
     * Sorts courses based on course creation date in the order of latest to oldest order.
     */
    public static void sortDetailedCoursesByCreationDate(List<CourseDetailsBundle> courses) {
        courses.sort(Comparator.comparing((CourseDetailsBundle obj) -> obj.course.createdAt).reversed());
    }

    public CourseStats getStats() {
        return stats;
    }

    public CourseAttributes getCourse() {
        return course;
    }
}
