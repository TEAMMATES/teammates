package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.StringHelper;
import teammates.storage.entity.CourseStudent;

/**
 * Migrates student's registration key to its encrypted version, if not yet encrypted.
 */
public class DataMigrationForUnencryptedKeyForStudents
        extends DataMigrationEntitiesBaseScript<CourseStudent> {

    public static void main(String[] args) {
        new DataMigrationForUnencryptedKeyForStudents().doOperationRemotely();
    }

    @Override
    protected Query<CourseStudent> getFilterQuery() {
        return ofy().load().type(CourseStudent.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(CourseStudent student) {
        try {
            StringHelper.decrypt(student.getRegistrationKey());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(CourseStudent student) {
        student.setRegistrationKey(StringHelper.encrypt(student.getRegistrationKey()));

        saveEntityDeferred(student);
    }

}
