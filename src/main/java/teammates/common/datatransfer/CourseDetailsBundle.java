package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents details of a course, including its students and feedback sessions.
 * <br> Contains:
 * <br> * statistics of teams, enrollments, registrations
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
    
    public ArrayList<FeedbackSessionDetailsBundle> feedbackSessions = new ArrayList<FeedbackSessionDetailsBundle>();
    public ArrayList<SectionDetailsBundle> sections = new ArrayList<SectionDetailsBundle>();
    //TODO: remove this as we do not allow loners anymore
    //Do not remove as we might cater for situations where there are no teams in future
    public ArrayList<StudentAttributes> loners = new ArrayList<StudentAttributes>();
    
    
    /**
     * Gets all FeedbackSessionAttributes in this CourseDetailsBundle
     * @return
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
    
    /**
     * Sorts courses based on course creation date in the order of latest to oldest order
     */
    public static void sortDetailedCoursesByCreationDate(List<CourseDetailsBundle> courses) {
        Collections.sort(courses, new Comparator<CourseDetailsBundle>() {
            public int compare(CourseDetailsBundle obj1, CourseDetailsBundle obj2) {
                return (-1) * obj1.course.createdAt.compareTo(obj2.course.createdAt);
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
