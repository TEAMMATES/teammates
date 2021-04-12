package teammates.ui.request;

/**
 * The basic request of modifying a course.
 */
public class CourseBasicRequest extends BasicRequest {
    private String courseName;
    private String timeZone;

    @Override
    public void validate() {
        assertTrue(courseName != null, "Course name should not be null");
        assertTrue(timeZone != null, "Time zone should not be null");
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
