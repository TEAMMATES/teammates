package teammates.client.scripts.sql;

import com.googlecode.objectify.cmd.Query;

import teammates.client.scripts.DataMigrationEntitiesBaseScript;
import teammates.storage.entity.Course;

/**
 * Index the newly-indexable fields of courses.
 */
public class IndexCourseFields extends DataMigrationEntitiesBaseScript<Course> {

    public static void main(String[] args) {
        new IndexCourseFields().doOperationRemotely();
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
        return true;
    }

    @Override
    protected void migrateEntity(Course course) {
        // Save without any update; this will build the previously non-existing indexes
        saveEntityDeferred(course);
    }
}
