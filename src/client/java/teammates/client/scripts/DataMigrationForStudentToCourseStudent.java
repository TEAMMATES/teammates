package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentWithOldRegistrationKeyAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.StudentsDb;
import teammates.storage.datastore.Datastore;

public class DataMigrationForStudentToCourseStudent extends RemoteApiClient {
    // TODO add mode to convert only recent students
    private static final boolean isPreview = true;
    private StudentsDb studentsDb = new StudentsDb();
    
    public static void main(String[] args) throws IOException {
        new DataMigrationForStudentToCourseStudent().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();

        List<StudentAttributes> students = getOldStudents();
        if (isPreview) {
            System.out.println("Creating a CourseStudent copy of students ...");
        }
        
        for (StudentAttributes student : students) {
            StudentWithOldRegistrationKeyAttributes studentToSave =
                    studentsDb.getStudentForCopyingToCourseStudent(student.course, student.email);
            
            if (isPreview) {
                System.out.println("Preview: copying " + studentToSave.getBackupIdentifier());
            } else {
                try {
                    studentsDb.createEntityWithoutExistenceCheck(studentToSave);
                    System.out.println("Created CourseStudent for " + studentToSave.getBackupIdentifier());
                } catch (InvalidParametersException e) {
                    System.out.println("Failed to create CourseStudent " + studentToSave.getIdentificationString());
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private List<StudentAttributes> getOldStudents() {
        return studentsDb.getAllOldStudents();
    }

    protected PersistenceManager getPm() {
        return Datastore.getPersistenceManager();
    }

}
