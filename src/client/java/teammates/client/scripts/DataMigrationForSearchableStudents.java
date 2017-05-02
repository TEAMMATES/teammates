package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.logic.core.StudentsLogic;

public class DataMigrationForSearchableStudents extends RemoteApiClient {

    private StudentsLogic studentsLogic = StudentsLogic.inst();

    public static void main(String[] args) throws IOException {
        DataMigrationForSearchableStudents migrator = new DataMigrationForSearchableStudents();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<StudentAttributes> allStudents = getAllStudents();
        for (StudentAttributes student : allStudents) {
            updateDocumentForStudent(student);
        }
    }

    private List<StudentAttributes> getAllStudents() {

        return studentsLogic.getAllStudents();
    }

    private void updateDocumentForStudent(StudentAttributes student) {
        studentsLogic.putDocument(student);
    }

}
