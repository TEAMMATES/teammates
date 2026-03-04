package teammates.client.scripts.sql;

import java.util.Objects;

import teammates.storage.entity.Course;

/**
 * Class for verifying course attributes.
 */
public class VerifyCourseAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Course, teammates.storage.sqlentity.Course> {

    public VerifyCourseAttributes() {
        super(Course.class,
                teammates.storage.sqlentity.Course.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Course sqlEntity) {
        return sqlEntity.getId();
    }

    public static void main(String[] args) {
        VerifyCourseAttributes script = new VerifyCourseAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        try {
            return sqlEntity.getId().equals(datastoreEntity.getUniqueId())
                    && sqlEntity.getName().equals(datastoreEntity.getName())
                    && sqlEntity.getTimeZone().equals(datastoreEntity.getTimeZone())
                    && sqlEntity.getInstitute().equals(datastoreEntity.getInstitute())
                    // && sqlEntity.getCreatedAt().equals(datastoreEntity.getCreatedAt())
                    && Objects.equals(sqlEntity.getDeletedAt(), datastoreEntity.getDeletedAt());
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }
}
