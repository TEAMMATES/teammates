package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import javax.jdo.JDOHelper;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.Instructor;

/**
 * Script to retrieve and put instructor entities without modification.
 * Originally used to generate indexes to allow the 'isArchived' field to be filtered.
 * Can be used for generating indexes for other fields in the future.
 *
 * <p>Uses low level DB calls for efficiency.
 */
public class DataMigrationForInstructorsCourseArchiving extends RemoteApiClient {

    public static void main(String[] args) throws IOException {
        DataMigrationForInstructorsCourseArchiving migrator = new DataMigrationForInstructorsCourseArchiving();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        // Recreate indexes for instructor entity

        String query = "select from " + Instructor.class.getName();
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) PM.newQuery(query).execute();
        int i = 0;
        for (Instructor instructor : instructorList) {
            // This makes persistence manager think that isArchived has been modified,
            // and makes it rewrite the entity to the database.
            // This re-creates indexes for the entity in the process.
            JDOHelper.makeDirty(instructor, "isArchived");
            System.out.println(++i + ". Touched " + instructor.getEmail());
        }

        // Generate registration key if null
        i = 0;
        for (Instructor instructor : instructorList) {
            instructor.setGeneratedKeyIfNull();
            System.out.println(++i + ". Added key for " + instructor.getEmail());
        }

        PM.close();
        System.out.println("Processed " + i + " insturctors");
    }
}
