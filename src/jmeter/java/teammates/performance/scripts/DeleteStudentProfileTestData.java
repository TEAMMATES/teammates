package teammates.performance.scripts;

/**
 *  Script to delete the Student Profile performance test data that is present in the datastore.
 */
public final class DeleteStudentProfileTestData extends DeleteTestData {

    private DeleteStudentProfileTestData() {
        // Intentional private constructor to prevent external instantiation
        pathToJson = "/studentProfile.json";
    }

    public static void main(String[] args) {
        DeleteTestData dataDeleter = new DeleteStudentProfileTestData();
        dataDeleter.deleteTestData(dataDeleter.getPathToJson());
    }

}
