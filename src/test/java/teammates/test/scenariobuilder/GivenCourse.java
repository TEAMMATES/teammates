package teammates.test.scenariobuilder;

import java.time.Instant;

import teammates.storage.entity.Course;

/**
 * Builder for Course entities used in test scenarios.
 */
public final class GivenCourse extends GivenBase<Course> {
    public GivenCourse(GivenData given, String courseId) {
        super(given);
        this.entity = defaultCourse(courseId);
    }

    /**
     * Sets the name for the course.
     */
    public GivenCourse name(String name) {
        entity.setName(name);
        return this;
    }

    /**
     * Sets the time zone for the course.
     */
    public GivenCourse timeZone(String timeZone) {
        entity.setTimeZone(timeZone);
        return this;
    }

    /**
     * Sets the institute for the course.
     */
    public GivenCourse institute(String institute) {
        entity.setInstitute(institute);
        return this;
    }

    /**
     * Marks the course as soft deleted.
     */
    public GivenCourse softDeleted() {
        entity.setDeletedAt(Instant.now());
        return this;
    }

    @Override
    void ensureConsistent() {
        // No mandatory relationships
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
