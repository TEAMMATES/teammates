package teammates.client.scripts;

import java.io.IOException;

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
    protected boolean isMigrationNeeded(Instructor instructor) throws Exception {
        return SanitizationHelper.isSanitizedHtml(instructor.getName());
    }

    @Override
    protected void migrateEntity(Instructor instructor) throws Exception {
        instructor.setName(SanitizationHelper.desanitizeIfHtmlSanitized(instructor.getName()));

        saveEntityDeferred(instructor);
    }
}
