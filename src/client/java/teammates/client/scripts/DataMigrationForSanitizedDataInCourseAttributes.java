package teammates.client.scripts;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Course;

/**
 * Script to desanitize content of {@link Course} if it is sanitized.
 */
public class DataMigrationForSanitizedDataInCourseAttributes
        extends DataMigrationEntitiesBaseScript<Course> {

    public DataMigrationForSanitizedDataInCourseAttributes() {
        numberOfScannedKey.set(0L);
        numberOfAffectedEntities.set(0L);
        numberOfUpdatedEntities.set(0L);
    }

    public static void main(String[] args) {
        DataMigrationForSanitizedDataInCourseAttributes migrator =
                new DataMigrationForSanitizedDataInCourseAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected Query<Course> getFilterQuery() {
        return ofy().load().type(Course.class);
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(Course course) {
        return isSanitizedHtml(course.getName());
    }

    @Override
    protected void migrateEntity(Course course) {
        course.setName(desanitizeIfHtmlSanitized(course.getName()));

        saveEntityDeferred(course);
    }
}
