package teammates.client.scripts;

import java.io.IOException;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Instructor;

/**
 * Script to desanitize name of {@link Instructor} if it is sanitized.
 */
public class DataMigrationForSanitizedInstructorName
        extends DataMigrationEntitiesBaseScript<Instructor> {

    public DataMigrationForSanitizedInstructorName() {
        numberOfScannedKey.set(0L);
        numberOfAffectedEntities.set(0L);
        numberOfUpdatedEntities.set(0L);
    }

    public static void main(String[] args) throws IOException {
        DataMigrationForSanitizedInstructorName migrator =
                new DataMigrationForSanitizedInstructorName();
        migrator.doOperationRemotely();
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
    protected String getLastPositionOfCursor() {
        return "";
    }

    @Override
    protected int getCursorInformationPrintCycle() {
        return 100;
    }

    @Override
    protected boolean isMigrationNeeded(Key<Instructor> key) throws Exception {
        Instructor instructor = ofy().load().key(key).now();

        return SanitizationHelper.isSanitizedHtml(instructor.getName());
    }

    @Override
    protected void migrateEntity(Key<Instructor> key) throws Exception {
        Instructor instructor = ofy().load().key(key).now();

        instructor.setName(SanitizationHelper.desanitizeIfHtmlSanitized(instructor.getName()));

        ofy().save().entity(instructor).now();
    }
}
