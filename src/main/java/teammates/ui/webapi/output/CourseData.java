package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * The API output format of a list of {@link CourseAttributes}.
 */
public class CourseData extends ApiOutput {
    /**
     * The output format of a course.
     */
    private final String courseId;
    private final String courseName;
    private final String creationDate;
    private String timeZone;

    public CourseData(CourseAttributes courseAttributes) {
        this.courseId = courseAttributes.getId();
        this.courseName = courseAttributes.getName();
        this.creationDate = courseAttributes.getCreatedAtDateString();
        this.timeZone = courseAttributes.getTimeZone().getId();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String s) {
        timeZone = s;
    }
}
