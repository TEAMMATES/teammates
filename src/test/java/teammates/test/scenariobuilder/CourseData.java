package teammates.test.scenariobuilder;

import java.time.Instant;

import teammates.storage.entity.Course;

/**
 * Builder for Course entities used in test scenarios.
 */
public final class CourseData {
    private Course course;

    public CourseData(String courseId) {
        this.course = defaultCourse(courseId);
    }

    public Course build() {
        return course;
    }

    /**
     * Sets the name for the course.
     */
    public CourseData name(String name) {
        course.setName(name);
        return this;
    }

    /**
     * Sets the time zone for the course.
     */
    public CourseData timeZone(String timeZone) {
        course.setTimeZone(timeZone);
        return this;
    }

    /**
     * Sets the institute for the course.
     */
    public CourseData institute(String institute) {
        course.setInstitute(institute);
        return this;
    }

    /**
     * Marks the course as soft deleted.
     */
    public CourseData softDeleted() {
        course.setDeletedAt(Instant.now());
        return this;
    }

    void ensureConsistent() {
        // No mandatory relationships
        return;
    }

    /**
     * Generates a default alias for a course.
     */
    public static String getDefaultAlias() {
        return "default";
    }

    private Course defaultCourse(String courseId) {
        return new Course(courseId, "Course Name", "UTC", "Institute Name");
    }
}
