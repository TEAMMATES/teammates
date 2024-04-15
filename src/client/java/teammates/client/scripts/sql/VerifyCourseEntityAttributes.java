package teammates.client.scripts.sql;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Class for verifying account attributes.
 */
@SuppressWarnings("PMD")
public class VerifyCourseEntityAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Course, teammates.storage.sqlentity.Course> {

    public VerifyCourseEntityAttributes() {
        super(Course.class,
                teammates.storage.sqlentity.Course.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Course sqlEntity) {
        return sqlEntity.getId();
    }

    public static void main(String[] args) {
        VerifyCourseEntityAttributes script = new VerifyCourseEntityAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        try {
            return verifyCourse(sqlEntity, datastoreEntity); // && verifySectionChain(sqlEntity);
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    private boolean verifyCourse(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        return sqlEntity.getId().equals(datastoreEntity.getUniqueId())
                && sqlEntity.getName().equals(datastoreEntity.getName())
                && sqlEntity.getTimeZone().equals(datastoreEntity.getTimeZone())
                && sqlEntity.getInstitute().equals(datastoreEntity.getInstitute())
                // && sqlEntity.getCreatedAt().equals(datastoreEntity.getCreatedAt())
                && datastoreEntity.getDeletedAt() == null ? sqlEntity.getDeletedAt() == null
                        : sqlEntity.getDeletedAt().equals(datastoreEntity.getDeletedAt());
    }

}
