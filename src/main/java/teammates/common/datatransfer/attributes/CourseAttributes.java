package teammates.common.datatransfer.attributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
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
    public Instant createdAt;
    public Instant deletedAt;
    private String id;
    private String name;
    private ZoneId timeZone;

    CourseAttributes(String courseId, String name, ZoneId timeZone) {
        this.id = SanitizationHelper.sanitizeTitle(courseId);
        this.name = SanitizationHelper.sanitizeTitle(name);
        this.timeZone = timeZone;
        this.createdAt = Instant.now();
        this.deletedAt = null;
    }

    /**
     * Returns new builder instance with default values for optional fields.
     *
     * <p>Following default values are set to corresponding attributes:
     * <ul>
     * <li>{@code createdAt = current date}</li>
     * </ul>
     *
     * @param courseId Id of the course.
     * @param name Name of the course.
     * @param timeZone Time zone of the course.
     * @return a {@code Builder} object that can be used to construct a {@code CourseAttributes} object
     */
    public static Builder builder(String courseId, String name, ZoneId timeZone) {
        return new Builder(courseId, name, timeZone);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    public String getCreatedAtDateString() {
        return TimeHelper.formatDateForInstructorCoursesPage(createdAt, timeZone);
    }

    public String getCreatedAtDateStamp() {
        return TimeHelper.formatDateTimeToIso8601Utc(createdAt);
    }

    public String getCreatedAtFullDateTimeString() {
        LocalDateTime localDateTime = TimeHelper.convertInstantToLocalDateTime(createdAt, timeZone);
        return TimeHelper.formatDateTimeForDisplay(localDateTime);
    }

    public String getDeletedAtDateString() {
        if (this.deletedAt == null) {
            return Const.DELETION_DATE_NOT_APPLICABLE;
        }
        return TimeHelper.formatDateForInstructorCoursesPage(deletedAt, timeZone);
    }

    public String getDeletedAtDateStamp() {
        if (this.deletedAt == null) {
            return Const.DELETION_DATE_NOT_APPLICABLE;
        }
        return TimeHelper.formatDateTimeToIso8601Utc(deletedAt);
    }

    public String getDeletedAtFullDateTimeString() {
        if (this.deletedAt == null) {
            return Const.DELETION_DATE_NOT_APPLICABLE;
        }
        LocalDateTime localDateTime = TimeHelper.convertInstantToLocalDateTime(deletedAt, timeZone);
        return TimeHelper.formatDateTimeForDisplay(localDateTime);
    }

    public void setDeletedAt() {
        this.deletedAt = Instant.now();
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void resetDeletedAt() {
        this.deletedAt = null;
    }

    public boolean isCourseDeleted() {
        return this.deletedAt != null;
    }

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public List<String> getInvalidityInfo() {

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<>();

        addNonEmptyError(validator.getInvalidityInfoForCourseId(getId()), errors);

        addNonEmptyError(validator.getInvalidityInfoForCourseName(getName()), errors);

        return errors;
    }

    @Override
    public Course toEntity() {
        return new Course(getId(), getName(), getTimeZone().getId(), createdAt, deletedAt);
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

        public Builder(String courseId, String name, ZoneId timeZone) {
            validateRequiredFields(courseId, name, timeZone);
            courseAttributes = new CourseAttributes(courseId, name, timeZone);
        }

        public Builder withCreatedAt(Instant createdAt) {
            if (createdAt != null) {
                courseAttributes.createdAt = createdAt;
            }

            return this;
        }

        public Builder withDeletedAt(Instant deletedAt) {
            courseAttributes.deletedAt = deletedAt;

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
