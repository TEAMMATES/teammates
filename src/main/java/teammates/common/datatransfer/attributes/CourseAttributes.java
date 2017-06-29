package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Course;

/**
 * The data transfer object for Course entities.
 */
public class CourseAttributes extends EntityAttributes<Course> implements Comparable<CourseAttributes> {

    private static Comparator<CourseAttributes> createdDateComparator = new Comparator<CourseAttributes>() {
        @Override
        public int compare(CourseAttributes course1, CourseAttributes course2) {
            if (course1.createdAt.compareTo(course2.createdAt) == 0) {
                return course1.getId().compareTo(course2.getId());
            }

            // sort by newest course first
            return -1 * course1.createdAt.compareTo(course2.createdAt);
        }
    };

    //Note: be careful when changing these variables as their names are used in *.json files.
    public Date createdAt;
    private String id;
    private String name;
    private String timeZone;

    public CourseAttributes() {
        // attributes to be set after construction
    }

    public CourseAttributes(String courseId, String name, String timeZone) {
        this.id = SanitizationHelper.sanitizeTitle(courseId);
        this.name = SanitizationHelper.sanitizeTitle(name);
        this.timeZone = timeZone;
    }

    public CourseAttributes(Course course) {
        this.id = course.getUniqueId();
        this.name = course.getName();
        this.timeZone = course.getTimeZone();
        this.createdAt = course.getCreatedAt();
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
        Collections.sort(courses, new Comparator<CourseAttributes>() {
            @Override
            public int compare(CourseAttributes c1, CourseAttributes c2) {
                return c1.getId().compareTo(c2.getId());
            }
        });
    }

    public static void sortByCreatedDate(List<CourseAttributes> courses) {
        Collections.sort(courses, createdDateComparator);
    }

}
