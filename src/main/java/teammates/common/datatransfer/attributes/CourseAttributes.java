package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;
import teammates.storage.entity.Course;

/**
 * The data transfer object for Course entities.
 */
public class CourseAttributes extends EntityAttributes<Course> implements Comparable<CourseAttributes> {

    //Note: be careful when changing these variables as their names are used in *.json files.
    public Date createdAt;
    private String id;
    private String name;
    private String timeZone;

    CourseAttributes(String courseId, String name, String timeZone) {
        this.id = SanitizationHelper.sanitizeTitle(courseId);
        this.name = SanitizationHelper.sanitizeTitle(name);
        this.timeZone = timeZone;
        this.createdAt = new Date();
    }

    /**
     * Returns new builder instance with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * <ul>
     * <li>{@code createdAt = current date}</li>
     * </ul>
     */
    public static Builder builder(String courseId, String name, String timeZone) {
        return new Builder(courseId, name, timeZone);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getCreatedAtDateString() {
        return TimeHelper.formatDateTimeForInstructorCoursesPage(createdAt);
    }

    public String getCreatedAtDateStamp() {
        return TimeHelper.formatDateToIso8601Utc(createdAt);
    }

    public String getCreatedAtFullDateTimeString() {
        return TimeHelper.formatTime12H(createdAt);
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public List<String> getInvalidityInfo() {

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<>();

        addNonEmptyError(validator.getInvalidityInfoForCourseId(getId()), errors);

        addNonEmptyError(validator.getInvalidityInfoForCourseName(getName()), errors);

        addNonEmptyError(validator.getInvalidityInfoForCourseTimeZone(getTimeZone()), errors);

        return errors;
    }

    @Override
    public Course toEntity() {
        return new Course(getId(), getName(), getTimeZone(), createdAt);
    }

    @Override
    public String toString() {
        return "[" + CourseAttributes.class.getSimpleName() + "] id: " + getId() + " name: " + getName()
               + " timeZone: " + getTimeZone();
    }

    @Override
    public String getIdentificationString() {
        return getId();
    }

    @Override
    public String getEntityTypeAsString() {
        return "Course";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + getId();
    }

    @Override
    public String getJsonString() {
        return JsonUtils.toJson(this, CourseAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        // no additional sanitization required
    }

    @Override
    public int compareTo(CourseAttributes o) {
        if (o == null) {
            return 0;
        }
        return o.createdAt.compareTo(createdAt);
    }

    public static void sortById(List<CourseAttributes> courses) {
        courses.sort(Comparator.comparing(CourseAttributes::getId));
    }

    public static void sortByCreatedDate(List<CourseAttributes> courses) {
        courses.sort(Comparator.comparing((CourseAttributes course) -> course.createdAt).reversed()
                .thenComparing(course -> course.getId()));
    }

    public static class Builder {
        private static final String REQUIRED_FIELD_CANNOT_BE_NULL = "Non-null value expected";
        private final CourseAttributes courseAttributes;

        public Builder(String courseId, String name, String timeZone) {
            validateRequiredFields(courseId, name, timeZone);
            courseAttributes = new CourseAttributes(courseId, name, timeZone);
        }

        public Builder withCreatedAt(Date createdAt) {
            if (createdAt != null) {
                courseAttributes.createdAt = createdAt;
            }

            return this;
        }

        public CourseAttributes build() {
            return courseAttributes;
        }

        private void validateRequiredFields(Object... objects) {
            for (Object object : objects) {
                Assumption.assertNotNull(REQUIRED_FIELD_CANNOT_BE_NULL, object);
            }
        }
    }
}
