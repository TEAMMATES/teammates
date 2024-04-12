package teammates.client.scripts.sql;

import teammates.storage.entity.CourseStudent;

/**
 * Class for verifying student attributes.
 */
@SuppressWarnings("PMD")
public class VerifyStudentAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<CourseStudent, teammates.storage.sqlentity.Student> {

    public VerifyStudentAttributes() {
        super(CourseStudent.class, teammates.storage.sqlentity.Student.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Student sqlEntity) {
        return CourseStudent.generateId(sqlEntity.getEmail(), sqlEntity.getCourse().getId());
    }

    public static void main(String[] args) {
        VerifyStudentAttributes script = new VerifyStudentAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Student sqlEntity, CourseStudent datastoreEntity) {
        // skips over students without accounts
        boolean doesgoogleIdMatch;
        if (sqlEntity.getGoogleId() == null) {
            doesgoogleIdMatch = true;
        } else {
            doesgoogleIdMatch = sqlEntity.getGoogleId().equals(datastoreEntity.getGoogleId());
        }

        try {
            return sqlEntity.getCourse().getId().equals(datastoreEntity.getCourseId())
                    && sqlEntity.getName().equals(datastoreEntity.getName())
                    && sqlEntity.getEmail().equals(datastoreEntity.getEmail())
                    && sqlEntity.getComments().equals(datastoreEntity.getComments())
                    && doesgoogleIdMatch
                    && sqlEntity.getTeamName().equals(datastoreEntity.getTeamName())
                    // && sqlEntity.getCreatedAt().equals(datastoreEntity.getCreatedAt())
                    // && sqlEntity.getUpdatedAt().equals(datastoreEntity.getUpdatedAt())
                    && sqlEntity.getRegKey().equals(datastoreEntity.getRegistrationKey());
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }
}
