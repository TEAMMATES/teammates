package teammates.ui.output;

import javax.annotation.Nullable;

import teammates.common.datatransfer.attributes.CourseAttributes;

/**
 * The API output format of a course.
 */
public class CourseData extends ApiOutput {

    private final String courseId;
    private final String courseName;
    private final String timeZone;
    private long creationTimestamp;
    private long deletionTimestamp;
    @Nullable
    private InstructorPrivilegeData privileges;

    public CourseData(CourseAttributes courseAttributes) {
        this.courseId = courseAttributes.getId();
        this.courseName = courseAttributes.getName();
        this.timeZone = courseAttributes.getTimeZone().getId();
        this.creationTimestamp = courseAttributes.getCreatedAt().toEpochMilli();
        if (courseAttributes.getDeletedAt() != null) {
            this.deletionTimestamp = courseAttributes.getDeletedAt().toEpochMilli();
        }
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

    public InstructorPrivilegeData getPrivileges() {
        return privileges;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public void setDeletionTimestamp(long deletionTimestamp) {
        this.deletionTimestamp = deletionTimestamp;
    }

    public void setPrivileges(InstructorPrivilegeData privileges) {
        this.privileges = privileges;
    }

    /**
     * Hides some attributes to student.
     */
    public void hideInformationForStudent() {
        setCreationTimestamp(0);
        setDeletionTimestamp(0);
    }
}
