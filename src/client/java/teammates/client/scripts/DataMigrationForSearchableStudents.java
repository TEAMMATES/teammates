package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.StudentAttributes;
import teammates.logic.api.Logic;
import teammates.storage.datastore.Datastore;

public class DataMigrationForSearchableStudents extends RemoteApiClient {
    
    private Logic logic = new Logic();
    
    public static void main(String[] args) throws IOException {
        DataMigrationForSearchableStudents migrator = new DataMigrationForSearchableStudents();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();

        List<StudentAttributes> allStudents = getAllStudents();
        for (StudentAttributes student : allStudents) {
            updateDocumentForStudent(student);
        }
    }
    
    private List<StudentAttributes> getAllStudents(){
       
        return logic.getAllStudents();
    }
    
    private void updateDocumentForStudent(StudentAttributes student){
        logic.putDocument(student);
    }

}
