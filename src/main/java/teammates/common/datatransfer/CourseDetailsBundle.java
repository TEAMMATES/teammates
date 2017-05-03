package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

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

    public ArrayList<FeedbackSessionDetailsBundle> feedbackSessions = new ArrayList<FeedbackSessionDetailsBundle>();
    public ArrayList<SectionDetailsBundle> sections = new ArrayList<SectionDetailsBundle>();

    public CourseDetailsBundle(CourseAttributes courseData) {
        this.course = courseData;
    }

    /**
     * Gets all FeedbackSessionAttributes in this CourseDetailsBundle.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsList() {
        List<FeedbackSessionAttributes> feedbackSessionAttributes = new ArrayList<FeedbackSessionAttributes>();
        for (FeedbackSessionDetailsBundle feedbackSessionDetails : feedbackSessions) {
            feedbackSessionAttributes.add(feedbackSessionDetails.feedbackSession);
        }
        return feedbackSessionAttributes;
    }

    public static void sortDetailedCoursesByCourseId(List<CourseDetailsBundle> courses) {
        Collections.sort(courses, new Comparator<CourseDetailsBundle>() {
            @Override
            public int compare(CourseDetailsBundle obj1, CourseDetailsBundle obj2) {
                return obj1.course.getId().compareTo(obj2.course.getId());
            }
        });
    }

    /**
     * Sorts courses based on course ID.
     */
    public static void sortDetailedCourses(List<CourseDetailsBundle> courses) {
        Collections.sort(courses, new Comparator<CourseDetailsBundle>() {
            @Override
            public int compare(CourseDetailsBundle obj1, CourseDetailsBundle obj2) {
                return obj1.course.getId().compareTo(obj2.course.getId());
            }
        });
    }

    /**
     * Sorts courses based on course creation date in the order of latest to oldest order.
     */
    public static void sortDetailedCoursesByCreationDate(List<CourseDetailsBundle> courses) {
        Collections.sort(courses, new Comparator<CourseDetailsBundle>() {
            @Override
            public int compare(CourseDetailsBundle obj1, CourseDetailsBundle obj2) {
                return obj2.course.createdAt.compareTo(obj1.course.createdAt);
            }
        });
    }

    public CourseStats getStats() {
        return stats;
    }

    public CourseAttributes getCourse() {
        return course;
    }
}
