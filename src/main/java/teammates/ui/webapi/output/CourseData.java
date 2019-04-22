package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * The API output format of a course.
 */
public class CourseData extends ApiOutput {

    private final String courseId;
    private final String courseName;
    private final String deletionDate;
    private final String timeZone;
    private final long creationTimestamp;

    public CourseData(CourseAttributes courseAttributes) {
        this.courseId = courseAttributes.getId();
        this.courseName = courseAttributes.getName();
        this.deletionDate = courseAttributes.getDeletedAtDateString();
        this.timeZone = courseAttributes.getTimeZone().getId();
        this.creationTimestamp = courseAttributes.getCreatedAt().toEpochMilli();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    }

    public String getDeletionDate() {
        return deletionDate;
    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public String getTimeZone() {
        return timeZone;
    }
}
