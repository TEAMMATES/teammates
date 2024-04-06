package teammates.client.scripts.sql;

import java.util.stream.Stream;

import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;

/**
 * Class for verifying section attributes.
 */
@SuppressWarnings("PMD")
public class VerifySectionAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Course, teammates.storage.sqlentity.Section> {

    public VerifySectionAttributes() {
        super(Course.class,
                teammates.storage.sqlentity.Section.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Section sqlEntity) {
        return sqlEntity.getCourse().getId();
    }

    public static void main(String[] args) {
        VerifySectionAttributes script = new VerifySectionAttributes();
        script.doOperationRemotely();
    }

    private Stream<String> getAllSectionNames(Course course) {
        return ofy()
                .load()
                .type(CourseStudent.class)
                .filter("courseId", course.getUniqueId())
                .list()
                .stream()
                .map(stu -> stu.getSectionName())
                .distinct();

    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Section sqlEntity, Course datastoreEntity) {
        return getAllSectionNames(datastoreEntity)
                .filter(sectionName -> {
                    try {
                        return sqlEntity.getName().equals(sectionName)
                                && sqlEntity.getCourse().getId().toString().equals(datastoreEntity.getUniqueId());
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
                .count() == 1;
    }
}
