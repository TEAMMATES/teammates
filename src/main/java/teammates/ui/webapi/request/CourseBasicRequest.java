package teammates.ui.webapi.request;

import teammates.ui.webapi.output.CourseData;

/**
 * The basic request of modifying a course.
 */
public class CourseBasicRequest extends BasicRequest {

    private CourseData storedData;

    public CourseData getCourseData() {
        return storedData;
    }

    public void setCourseData(CourseData cd) {
        storedData = cd;
    }

    @Override
    public void validate() {
        assertTrue(storedData != null, "CourseData should not be null");
        assertTrue(storedData.getCourseName() != null, "Course name should not be null");
        assertTrue(storedData.getCourseId() != null, "Course ID should not be null");
        assertTrue(storedData.getTimeZone() != null, "Course time zone should not be null");
    }
}
