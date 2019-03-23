package teammates.performance.scripts;

/**
 *  Script to delete the student profile test data that was created in the datastore.
 */
public class DeleteStudentProfileTestData extends DeleteTestData {

    private DeleteStudentProfileTestData() {
        // Intentional private constructor to prevent external instantiation
    }

    public static void main(String[] args) {
        DeleteTestData dataDeleter = new DeleteStudentProfileTestData();
        dataDeleter.deleteTestData("/studentProfile.json");
    }

}
