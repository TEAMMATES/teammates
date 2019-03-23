package teammates.performance.scripts;

/**
 * Base class to create the CSV config data for Student Profile performance test.
 */
public final class CreateStudentProfileTestConfigData extends CreateTestConfigData {

    private CreateStudentProfileTestConfigData() {
        // Intentional private constructor to prevent external instantiation
        pathToOutputCsv = "/studentProfileConfig.csv";
    }

    public static void main(String[] args) {
        CreateTestConfigData configDataCreator = new CreateStudentProfileTestConfigData();
        JSONObject jsonData = dataCreator.createJsonData();
        writeJsonDataToFile(jsonData, configDataCreator.getPathToOutputCsv());
    }
}
