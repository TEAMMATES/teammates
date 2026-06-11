package teammates.test.scenariobuilder;

import java.time.Instant;

import teammates.storage.entity.Course;
import teammates.storage.entity.Institute;

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
     * Sets the institute for the course, referenced by its alias.
     */
    public GivenCourse institute(String instituteAlias) {
        Institute institute = given.getOrCreate(instituteAlias, given.dataBundle.institutes, given::institute);
        institute.addCourse(entity);
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
        if (entity.getInstituteId() == null) {
            this.institute("default");
        }
    }

    /**
     * Generates a default alias for a course.
     */
    public static String getDefaultAlias() {
        return "default";
    }

    private Course defaultCourse(String courseId) {
        return new Course(courseId, "Course Name", "UTC");
    }
}
