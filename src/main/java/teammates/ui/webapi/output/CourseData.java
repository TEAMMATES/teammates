package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * The API output format of a course.
 */
public class CourseData extends ApiOutput {

    private final String courseId;
    private final String courseName;
    private final String creationDate;
    private final String deletionDate;
    private final String timeZone;

    public CourseData(CourseAttributes courseAttributes) {
        this.courseId = courseAttributes.getId();
        this.courseName = courseAttributes.getName();
        this.creationDate = courseAttributes.getCreatedAtDateString();
        this.deletionDate = courseAttributes.getDeletedAtDateString();
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

    public String getDeletionDate() {
        return deletionDate;
    }

    public String getTimeZone() {
        return timeZone;
    }
}
