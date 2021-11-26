package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.StringHelper;
import teammates.storage.entity.Instructor;

/**
 * Migrates instructor's registration key to its encrypted version, if not yet encrypted.
 */
public class DataMigrationForUnencryptedKeyForInstructors
        extends DataMigrationEntitiesBaseScript<Instructor> {

    public static void main(String[] args) {
        new DataMigrationForUnencryptedKeyForInstructors().doOperationRemotely();
    }

    @Override
    protected Query<Instructor> getFilterQuery() {
        return ofy().load().type(Instructor.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(Instructor instructor) {
        try {
            StringHelper.decrypt(instructor.getRegistrationKey());
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(Instructor instructor) {
        instructor.setRegistrationKey(StringHelper.encrypt(instructor.getRegistrationKey()));

        saveEntityDeferred(instructor);
    }

}
