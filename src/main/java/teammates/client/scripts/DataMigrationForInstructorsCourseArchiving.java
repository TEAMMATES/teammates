package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import javax.jdo.JDOHelper;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Instructor;

/**
 * Script to retrieve and put instructor entities without modification.
 * Originally used to generate indexes to allow the 'isArchived' field to be filtered.
 * Can be used for generating indexes for other fields in the future.
 */
public class DataMigrationForInstructorsCourseArchiving extends RemoteApiClient {
    
    public static void main(String[] args) throws IOException {
        DataMigrationForInstructorsCourseArchiving migrator = new DataMigrationForInstructorsCourseArchiving();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();
        
        String query = "select from " + Instructor.class.getName();
        
        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) Datastore.getPersistenceManager()
                .newQuery(query).execute();
        
        for (Instructor instructor : instructorList) {
            // This makes persistence manager think that isArchived has been modified,
            // and makes it rewrite the entity to the database.
            // This re-creates indexes for the entity in the process.
            JDOHelper.makeDirty(instructor, "isArchived");
        }
        
        Datastore.getPersistenceManager().close();
    }
}
