package teammates.ui.output;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.storage.entity.Course;

/**
 * The API output format of a course.
 */
public class CourseData implements ApiOutput {

    private final String courseId;
    private final String courseName;
    private final String timeZone;
    private final String institute;
    private final String country;
    private final UUID instituteId;
    private long creationTimestamp;
    private long deletionTimestamp;

    @JsonCreator
    private CourseData(String courseId, String courseName, String timeZone, String institute, String country,
            UUID instituteId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.timeZone = timeZone;
        this.institute = institute;
        this.country = country;
        this.instituteId = instituteId;
    }

    public CourseData(Course course) {
        this.courseId = course.getId();
        this.courseName = course.getName();
        this.timeZone = course.getTimeZone();
        this.institute = course.getInstitute().getName();
        this.country = course.getInstitute().getCountry();
        this.instituteId = course.getInstitute().getId();
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

    public String getCountry() {
        return country;
    }

    public UUID getInstituteId() {
        return instituteId;
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

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public void setDeletionTimestamp(long deletionTimestamp) {
        this.deletionTimestamp = deletionTimestamp;
    }
}
