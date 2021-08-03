package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Instructor;

/**
 * Script to desanitize content of {@link Instructor} if it is sanitized.
 */
public class DataMigrationForSanitizedDataInInstructorAttributes
        extends DataMigrationEntitiesBaseScript<Instructor> {

    public DataMigrationForSanitizedDataInInstructorAttributes() {
        numberOfScannedKey.set(0L);
        numberOfAffectedEntities.set(0L);
        numberOfUpdatedEntities.set(0L);
    }

    public static void main(String[] args) {
        DataMigrationForSanitizedDataInInstructorAttributes migrator =
                new DataMigrationForSanitizedDataInInstructorAttributes();
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
    protected boolean isMigrationNeeded(Instructor instructor) {
        if (isSanitizedHtml(instructor.getRole())) {
            logError(String.format("Instructor %s has unsanitized role %s, this should not happen",
                    instructor.getUniqueId(), instructor.getRole()));
        }

        return isSanitizedHtml(instructor.getDisplayedName());
    }

    @Override
    protected void migrateEntity(Instructor instructor) {
        instructor.setDisplayedName(desanitizeIfHtmlSanitized(instructor.getDisplayedName()));

        saveEntityDeferred(instructor);
    }
}
