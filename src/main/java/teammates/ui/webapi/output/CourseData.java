package teammates.ui.webapi.output;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * The API output format of a course.
 */
public class CourseData extends ApiOutput {

    private final String courseId;
    private final String courseName;
    private final String timeZone;
    private final long creationTimestamp;
    private final long deletionTimestamp;

    public CourseData(CourseAttributes courseAttributes) {
        this.courseId = courseAttributes.getId();
        this.courseName = courseAttributes.getName();
        this.timeZone = courseAttributes.getTimeZone().getId();
        this.creationTimestamp = courseAttributes.getCreatedAt().toEpochMilli();
        this.deletionTimestamp = courseAttributes.getDeletedAt().toEpochMilli();
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public long getDeletionTimestamp() {
        return deletionTimestamp;
    }
}
