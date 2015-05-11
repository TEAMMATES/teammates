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
    public ArrayList<FeedbackSessionAttributes> feedbackSessions = new ArrayList<FeedbackSessionAttributes>();
    public ArrayList<SectionDetailsBundle> sections = new ArrayList<SectionDetailsBundle>();
    public ArrayList<StudentAttributes> loners = new ArrayList<StudentAttributes>();
    
    /**
     * Sorts courses based on course ID
     */ 
    public static void sortSummarizedCoursesByCourseId(List<CourseSummaryBundle> courses) {
        Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
            public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
                return obj1.course.id.compareTo(obj2.course.id);
            }
        });
    }
    
    /**
     * Sorts courses based on course name
     */ 
    public static void sortSummarizedCoursesByCourseName(List<CourseSummaryBundle> courses) {
        Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
            public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
                return obj1.course.name.compareTo(obj2.course.name);
            }
        });
    }
    
    /**
     * Sorts courses based on course creation date in the order of latest to oldest order
     */ 
    public static void sortSummarizedCoursesByCreationDate(List<CourseSummaryBundle> courses) {
        Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
            public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
                return (-1) * obj1.course.createdAt.compareTo(obj2.course.createdAt);
            }
        });
    }
    
    public static void sortSummarizedCourses(List<CourseSummaryBundle> courses) {
        Collections.sort(courses, new Comparator<CourseSummaryBundle>() {
            public int compare(CourseSummaryBundle obj1, CourseSummaryBundle obj2) {
                return obj1.course.id.compareTo(obj2.course.id);
            }
        });
    }
}
