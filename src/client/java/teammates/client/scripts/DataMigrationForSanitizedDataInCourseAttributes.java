package teammates.client.scripts;

import java.io.IOException;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.SanitizationHelper;
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

    public static void main(String[] args) throws IOException {
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
    protected boolean isMigrationNeeded(Course course) throws Exception {
        return SanitizationHelper.isSanitizedHtml(course.getName());
    }

    @Override
    protected void migrateEntity(Course course) throws Exception {
        course.setName(SanitizationHelper.desanitizeIfHtmlSanitized(course.getName()));

        saveEntityDeferred(course);
    }
}
