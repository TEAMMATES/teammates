package teammates.test.scenariobuilder;

import java.time.Instant;

import teammates.storage.entity.Course;

public final class CourseData {
    private Course course;

    public CourseData(String courseId) {
        course = defaultCourse(courseId);
    }

    public Course build() {
        return course;
    }

    public CourseData name(String name) {
        course.setName(name);
        return this;
    }

    public CourseData timeZone(String timeZone) {
        course.setTimeZone(timeZone);
        return this;
    }

    public CourseData institute(String institute) {
        course.setInstitute(institute);
        return this;
    }

    public CourseData softDeleted() {
        course.setDeletedAt(Instant.now());
        return this;
    }

    private Course defaultCourse(String courseId) {
        return new Course(courseId, "Course Name", "UTC", "Institute Name");
    }
}
