package teammates.client.scripts;

import java.io.IOException;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.StudentProfile;

/**
 * Script to delete all orphaned StudentProfile entity.
 */
public class DataMigrationForOrphanedStudentProfile extends DataMigrationWithCheckpointForEntities<StudentProfile> {

    public static void main(String[] args) throws IOException {
        new DataMigrationForOrphanedStudentProfile().doOperationRemotely();
    }

    @Override
    protected Query<StudentProfile> getFilterQuery() {
        return ofy().load().type(StudentProfile.class);
    }

    @Override
    protected String getLastPositionOfCursor() {
        return "";
    }

    @Override
    protected int getCursorInformationPrintCycle() {
        // Till 23/08/2018, we have around 61363 StudentProfile entities and therefore
        // 500 could be a good batch size.
        return 500;
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(Key<StudentProfile> spKey) throws Exception {
        // orphaned student profile doesn't have parent key
        return spKey.getParent() == null;
    }

    @Override
    protected void migrateEntity(Key<StudentProfile> spKey) throws Exception {
        println("Deleting orphaned StudentProfile now! " + spKey);
        ofy().delete().key(spKey).now();
    }
}
