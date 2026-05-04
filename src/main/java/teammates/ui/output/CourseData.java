package teammates.ui.output;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.storage.entity.Course;

/**
 * The API output format of a course.
 */
public class CourseData extends ApiOutput {

    private final String courseId;
    private final String courseName;
    private final String timeZone;
    private final String institute;
    private long creationTimestamp;
    private long deletionTimestamp;
    @Nullable
    private InstructorPermissionSet privileges;

    @JsonCreator
    private CourseData(String courseId, String courseName, String timeZone, String institute) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.timeZone = timeZone;
        this.institute = institute;
    }

    public CourseData(Course course) {
        this.courseId = course.getId();
        this.courseName = course.getName();
        this.timeZone = course.getTimeZone();
        this.institute = course.getInstitute();
        this.creationTimestamp = course.getCreatedAt().toEpochMilli();
        if (course.getDeletedAt() != null) {
            this.deletionTimestamp = course.getDeletedAt().toEpochMilli();
        }
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getInstitute() {
        return institute;
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

    public InstructorPermissionSet getPrivileges() {
        return privileges;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public void setDeletionTimestamp(long deletionTimestamp) {
        this.deletionTimestamp = deletionTimestamp;
    }

    public void setPrivileges(InstructorPermissionSet privileges) {
        this.privileges = privileges;
    }

    /**
     * Hides some attributes to student.
     */
    public void hideInformationForStudent() {
        setDeletionTimestamp(0);
    }
}
