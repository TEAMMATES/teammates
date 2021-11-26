package teammates.client.scripts;

import java.lang.reflect.Field;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Instructor;

/**
 * Script to change all null value to false in the isArchived field for Instructor entity.
 */
public class DataMigrationForInstructorNullIsArchivedField extends DataMigrationEntitiesBaseScript<Instructor> {

    public static void main(String[] args) {
        new DataMigrationForInstructorNullIsArchivedField().doOperationRemotely();
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
            Field isArchivedField = instructor.getClass().getDeclaredField("isArchived");
            isArchivedField.setAccessible(true);
            return isArchivedField.get(instructor) == null;
        } catch (ReflectiveOperationException e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(Instructor instructor) {
        instructor.setIsArchived(instructor.getIsArchived());

        saveEntityDeferred(instructor);
    }

}
