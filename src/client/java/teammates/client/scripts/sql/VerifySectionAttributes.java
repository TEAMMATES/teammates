package teammates.client.scripts.sql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Section;

/**
 * Class for verifying section attributes.
 */
@SuppressWarnings("PMD")
public class VerifySectionAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Course, teammates.storage.sqlentity.Course> {

    public VerifySectionAttributes() {
        super(Course.class,
                teammates.storage.sqlentity.Course.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Course sqlEntity) {
        return sqlEntity.getId();
    }

    public static void main(String[] args) {
        VerifySectionAttributes script = new VerifySectionAttributes();
        script.doOperationRemotely();
    }

    private Set<String> getAllSectionNames(Course course) {
        return ofy()
                .load()
                .type(CourseStudent.class)
                .filter("courseId", course.getUniqueId())
                .list()
                .stream()
                .map(stu -> {
                    String name = stu.getSectionName();
                    return name == null || name.isEmpty() ? "None" : name;
                })
                .distinct()
                .collect(Collectors.toCollection(HashSet::new));

    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        List<Section> sections = sqlEntity.getSections();
        Set<String> newSectionNames = new HashSet<>(
                sections.stream().map(Section::getName).collect(Collectors.toList()));
        Set<String> oldSectionNames = getAllSectionNames(datastoreEntity);

        return sqlEntity.getId().equals(datastoreEntity.getUniqueId())
                && sections.size() == newSectionNames.size()
                && newSectionNames.equals(oldSectionNames);

    }
}
