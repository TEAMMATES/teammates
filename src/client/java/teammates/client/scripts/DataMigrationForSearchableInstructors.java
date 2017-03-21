package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.logic.core.InstructorsLogic;

public class DataMigrationForSearchableInstructors extends RemoteApiClient {

    private InstructorsLogic instructorsLogic = InstructorsLogic.inst();

    public static void main(String[] args) throws IOException {
        DataMigrationForSearchableInstructors migrator = new DataMigrationForSearchableInstructors();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<InstructorAttributes> allInstructors = getAllInstructors();
        for (InstructorAttributes instructor : allInstructors) {
            updateDocumentForInstructor(instructor);
        }
    }

    @SuppressWarnings("deprecation")
    private List<InstructorAttributes> getAllInstructors() {

        return instructorsLogic.getAllInstructors();
    }

    private void updateDocumentForInstructor(InstructorAttributes instructor) {
        instructorsLogic.putDocument(instructor);
    }

}
